package com.gagak.farmshields.ui.profile

import android.Manifest
import android.app.Activity
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Bundle
import android.os.Environment
import android.provider.MediaStore
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.content.FileProvider
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import com.bumptech.glide.Glide
import com.gagak.farmshields.R
import com.gagak.farmshields.core.domain.model.viewmodel.user.UserViewModel
import com.gagak.farmshields.core.utils.PermissionUtils
import com.gagak.farmshields.databinding.FragmentProfileUpdateBinding
import com.google.android.material.bottomsheet.BottomSheetDialog
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody
import org.koin.androidx.viewmodel.ext.android.viewModel
import java.io.File
import java.io.FileOutputStream
import java.io.IOException
import java.text.SimpleDateFormat
import java.util.*

class ProfileUpdateFragment : Fragment() {
    private var _binding: FragmentProfileUpdateBinding? = null
    private val binding get() = _binding!!
    private val viewModel: UserViewModel by viewModel()
    private var currentPhotoPath: String = ""
    private var selectedImageUri: Uri? = null

    private val launcherGallery =
        registerForActivityResult(ActivityResultContracts.PickVisualMedia()) { uri: Uri? ->
            uri?.let {
                selectedImageUri = it
                binding.profileImage.setImageURI(it)
            }
        }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentProfileUpdateBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupObservers()
        viewModel.getUser()
        binding.apply {
            // Disable text fields by default
            RUsername.isEnabled = false
            REmail.isEnabled = false
            RPassword.isEnabled = false
            // Enable text fields when the button is clicked
            enableEditButton.setOnClickListener {
                binding.RUsername.isEnabled = true
                binding.RPassword.isEnabled = true
            }

            RUpdate.setOnClickListener {
                val name = if (binding.RUsername.isEnabled && binding.RUsername.text.toString().isNotEmpty()) {
                    RequestBody.create("text/plain".toMediaTypeOrNull(), binding.RUsername.text.toString())
                } else null

                val address = if (binding.RPassword.isEnabled && binding.RPassword.text.toString().isNotEmpty()) {
                    RequestBody.create("text/plain".toMediaTypeOrNull(), binding.RPassword.text.toString())
                } else null

                // Convert URI to File and create MultipartBody.Part
                val image: MultipartBody.Part? = selectedImageUri?.let {
                    val file = uriToFile(it)
                    val requestFile = RequestBody.create("image/*".toMediaTypeOrNull(), file)
                    MultipartBody.Part.createFormData("image", file.name, requestFile)
                }

                // Update user with image, name, and address
                viewModel.updateUser(image, name, address)
            }
            // Set click and long click listeners for the profile image and icon
            profileImage.setOnClickListener {
                showBottomSheet()
            }
            iconImage.setOnClickListener {
                showBottomSheet()
            }

            profileImage.setOnLongClickListener {
                showBottomSheet()
                true
            }

            iconImage.setOnLongClickListener {
                showBottomSheet()
                true
            }
        }

    }

    private fun setupObservers() {
        viewModel.userResponse.observe(viewLifecycleOwner, Observer { response ->
            response?.data?.user?.let { userDetail ->
                binding.apply {
                    RUsername.setText(userDetail.name)
                    REmail.setText(userDetail.email)
                    RPassword.setText(userDetail.address)
                    Glide.with(this@ProfileUpdateFragment)
                        .load(userDetail.image)
                        .into(profileImage)
                }
            }
        })

        viewModel.updateResponse.observe(viewLifecycleOwner, Observer { response ->
            response?.let {
                Toast.makeText(requireContext(), it.message, Toast.LENGTH_SHORT).show()
            }
        })

        viewModel.loading.observe(viewLifecycleOwner, Observer { isLoading ->
            binding.progressBar.visibility = if (isLoading) View.VISIBLE else View.GONE
        })

        viewModel.error.observe(viewLifecycleOwner, Observer { error ->
            error?.let {
                Toast.makeText(requireContext(), it, Toast.LENGTH_SHORT).show()
            }
        })
    }

    private fun showBottomSheet() {
        val bottomSheetView = layoutInflater.inflate(R.layout.bottom_sheet_option, null)
        val bottomSheetDialog = BottomSheetDialog(requireContext())
        bottomSheetDialog.setContentView(bottomSheetView)

        bottomSheetView.findViewById<View>(R.id.option_camera).setOnClickListener {
            if (PermissionUtils.checkPermission(requireContext(), Manifest.permission.CAMERA)) {
                openCamera()
            } else {
                requestCameraPermission()
            }
            bottomSheetDialog.dismiss()
        }

        bottomSheetView.findViewById<View>(R.id.option_gallery).setOnClickListener {
            val storagePermissions = getStoragePermission()
            if (PermissionUtils.checkPermission(requireContext(), storagePermissions[0])) {
                openGallery()
            } else {
                requestGalleryPermission()
            }
            bottomSheetDialog.dismiss()
        }

        bottomSheetDialog.show()
    }

    private fun getStoragePermission(): Array<String> {
        return when {
            android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.UPSIDE_DOWN_CAKE -> {
                arrayOf(
                    Manifest.permission.READ_MEDIA_IMAGES,
                    Manifest.permission.READ_MEDIA_VIDEO,
                    Manifest.permission.READ_MEDIA_VISUAL_USER_SELECTED
                )
            }
            android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.TIRAMISU -> {
                arrayOf(
                    Manifest.permission.READ_MEDIA_IMAGES,
                    Manifest.permission.READ_MEDIA_VIDEO
                )
            }
            else -> {
                arrayOf(Manifest.permission.READ_EXTERNAL_STORAGE)
            }
        }
    }

    private fun requestCameraPermission() {
        if (PermissionUtils.shouldShowRationale(requireActivity(), Manifest.permission.CAMERA)) {
            Toast.makeText(context, "Camera permission is required to take pictures", Toast.LENGTH_SHORT).show()
        }
        PermissionUtils.requestPermission(requireActivity(), Manifest.permission.CAMERA, PermissionUtils.REQUEST_CAMERA_PERMISSION)
    }

    private fun requestGalleryPermission() {
        val storagePermissions = getStoragePermission()
        if (PermissionUtils.shouldShowRationale(requireActivity(), storagePermissions[0])) {
            Toast.makeText(context, "Storage permission is required to select pictures", Toast.LENGTH_SHORT).show()
        }
        PermissionUtils.requestPermission(requireActivity(), storagePermissions[0], PermissionUtils.REQUEST_GALLERY_PERMISSION)
    }

    private fun openCamera() {
        val intent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
        val photoFile: File? = try {
            createImageFile()
        } catch (ex: IOException) {
            null
        }

        photoFile?.also {
            val photoURI = FileProvider.getUriForFile(requireContext(), "${requireContext().packageName}.fileprovider", it)
            intent.putExtra(MediaStore.EXTRA_OUTPUT, photoURI)
            currentPhotoPath = it.absolutePath
        }

        startActivityForResult(intent, PermissionUtils.REQUEST_CAMERA_PERMISSION)
    }

    private fun openGallery() {
        launcherGallery.launch(PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageAndVideo))
    }

    @Throws(IOException::class)
    private fun createImageFile(): File {
        val timeStamp: String = SimpleDateFormat("yyyyMMdd_HHmmss").format(Date())
        val storageDir = File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES), "Farmshields")
        if (!storageDir.exists()) {
            storageDir.mkdirs()
        }
        return File.createTempFile("FARMSHIELDS_${timeStamp}_", ".jpg", storageDir).apply {
            currentPhotoPath = absolutePath
        }
    }

    private fun uriToFile(selectedImageUri: Uri): File {
        val contentResolver = requireContext().contentResolver
        val file = createImageFile()
        val inputStream = contentResolver.openInputStream(selectedImageUri) ?: return file
        val outputStream = FileOutputStream(file)
        inputStream.copyTo(outputStream)
        inputStream.close()
        outputStream.close()
        return file
    }

    @Deprecated("Deprecated in Java")
    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        when (requestCode) {
            PermissionUtils.REQUEST_CAMERA_PERMISSION -> {
                if ((grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED)) {
                    openCamera()
                } else {
                    Toast.makeText(context, "Camera permission denied", Toast.LENGTH_SHORT).show()
                }
            }
            PermissionUtils.REQUEST_GALLERY_PERMISSION -> {
                if ((grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED)) {
                    openGallery()
                } else {
                    Toast.makeText(context, "Storage permission denied", Toast.LENGTH_SHORT).show()
                }
            }
        }
    }

    @Deprecated("Deprecated in Java")
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (resultCode == Activity.RESULT_OK) {
            if (requestCode == PermissionUtils.REQUEST_CAMERA_PERMISSION) {
                val file = File(currentPhotoPath)
                val uri = FileProvider.getUriForFile(requireContext(), "${requireContext().packageName}.fileprovider", file)
                binding.profileImage.setImageURI(uri)
                selectedImageUri = uri
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
