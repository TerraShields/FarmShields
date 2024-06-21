package com.gagak.farmshields.ui.report

import android.Manifest
import android.app.Activity
import android.app.AlertDialog
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.Intent.ACTION_MEDIA_SCANNER_SCAN_FILE
import android.content.IntentFilter
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.location.LocationManager
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.os.Environment
import android.provider.MediaStore
import android.provider.Settings
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.app.ActivityCompat
import androidx.core.content.FileProvider
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.navigation.fragment.findNavController
import com.bumptech.glide.Glide
import com.gagak.farmshields.R
import com.gagak.farmshields.core.data.adapter.SignAdapter
import com.gagak.farmshields.core.domain.model.viewmodel.main.MainViewModel
import com.gagak.farmshields.core.domain.model.viewmodel.user.UserViewModel
import com.gagak.farmshields.core.utils.GeocodingUtils
import com.gagak.farmshields.core.utils.PermissionUtils
import com.gagak.farmshields.databinding.FragmentReportBinding
import com.google.android.gms.location.*
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

class ReportFragment : Fragment() {
    private val viewModel: MainViewModel by viewModel()
    private val userViewModel: UserViewModel by viewModel()
    private var _binding: FragmentReportBinding? = null
    private val binding get() = _binding!!
    private lateinit var currentPhotoPath: String
    private var selectedImageUri: Uri? = null
    private lateinit var fusedLocationClient: FusedLocationProviderClient
    private lateinit var locationRequest: LocationRequest
    private lateinit var locationCallback: LocationCallback
    private var latitude: String = ""
    private var longitude: String = ""

    private val launcherGallery =
        registerForActivityResult(ActivityResultContracts.PickVisualMedia()) { uri: Uri? ->
            uri?.let {
                selectedImageUri = it
                binding.ivPreviewImage.setImageURI(it)
                binding.cardViewPreview.visibility = View.VISIBLE
            }
        }

    private val locationReceiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context?, intent: Intent?) {
            if (intent?.action == LocationManager.PROVIDERS_CHANGED_ACTION) {
                checkGPSAndFetchLocation()
            }
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentReportBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(requireActivity())
        checkGPSAndFetchLocation()
        userViewModel.getUser()

        userViewModel.userResponse.observe(viewLifecycleOwner, Observer { response ->
            response?.data?.user?.let { userDetail ->
                binding.apply {
                    tvUserName.text = userDetail.name
                    tvUserName.visibility = View.VISIBLE
                    ivProfileImage.visibility = View.VISIBLE
                    Glide.with(this@ReportFragment)
                        .load(userDetail.image)
                        .into(ivProfileImage)
                }
            }
        })

        binding.apply {
            ivAddSign.setOnClickListener {
                showBottomSheet()
            }
            ivCamera.setOnClickListener {
                if (PermissionUtils.checkPermission(requireContext(), Manifest.permission.CAMERA)) {
                    openCamera()
                } else {
                    requestCameraPermission()
                }
            }
            ivAddImage.setOnClickListener {
                val storagePermissions = getStoragePermission()
                if (PermissionUtils.checkPermission(requireContext(), storagePermissions[0])) {
                    openGallery()
                } else {
                    requestGalleryPermission()
                }
            }
            ivAddLocation.setOnClickListener {
                if (PermissionUtils.checkPermission(
                        requireContext(),
                        Manifest.permission.ACCESS_FINE_LOCATION
                    )
                ) {
                    checkGPSAndFetchLocation()
                } else {
                    requestLocationPermission()
                }
            }
            btnPost.setOnClickListener {
                selectedImageUri?.let { uri ->
                    uploadReport(uri)
                } ?: Toast.makeText(requireContext(), "Please select an image", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun uploadReport(uri: Uri) {
        val file = uriToFile(uri, requireContext())
        val requestFile = RequestBody.create("image/*".toMediaTypeOrNull(), file)
        val body = MultipartBody.Part.createFormData("image", file.name, requestFile)
        val latitudePart = RequestBody.create("text/plain".toMediaTypeOrNull(), latitude)
        val longitudePart = RequestBody.create("text/plain".toMediaTypeOrNull(), longitude)
        val signPart = RequestBody.create("text/plain".toMediaTypeOrNull(), binding.tvSign.text.toString())

        binding.progressBar.visibility = View.VISIBLE // Show progress bar

        viewModel.report(body, latitudePart, longitudePart, signPart).observe(viewLifecycleOwner) { response ->
            binding.progressBar.visibility = View.GONE // Hide progress bar
            if (response != null && response.isSuccessful) {
                Toast.makeText(requireContext(), "Report uploaded successfully", Toast.LENGTH_SHORT).show()
                // Navigate to HomeFragment
                findNavController().navigate(R.id.action_reportFragment_to_homeFragment)
            } else {
                val errorMessage = response?.errorBody()?.string() ?: "Failed to upload report"
                Toast.makeText(requireContext(), errorMessage, Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun uriToFile(selectedImageUri: Uri, context: Context): File {
        val contentResolver = context.contentResolver
        val file = createImageFile()
        val inputStream = contentResolver.openInputStream(selectedImageUri) ?: return file
        val outputStream = FileOutputStream(file)
        inputStream.copyTo(outputStream)
        inputStream.close()
        outputStream.close()
        return file
    }

    private fun showBottomSheet() {
        val bottomSheetView = layoutInflater.inflate(R.layout.bottom_sheet_sign, null)
        val bottomSheetDialog = BottomSheetDialog(requireContext())
        bottomSheetDialog.setContentView(bottomSheetView)
        val signs = resources.getStringArray(R.array.sign_array).toList()
        val recyclerView: RecyclerView = bottomSheetView.findViewById(R.id.rvSigns)
        recyclerView.layoutManager = LinearLayoutManager(context)
        recyclerView.adapter = SignAdapter(signs) { sign ->
            // Handle the click event, e.g., display the selected sign in ReportFragment
            binding.tvSign.apply {
                text = sign
                visibility = View.VISIBLE
                when (sign) {
                    "Bahaya" -> setBackgroundResource(R.drawable.danger_sign)
                    "Aman" -> setBackgroundResource(R.drawable.safe_sign)
                    "Peringatan" -> setBackgroundResource(R.drawable.warning_sign)
                }
            }
            bottomSheetDialog.dismiss()
        }
        bottomSheetDialog.show()
    }

    private fun getStoragePermission(): Array<String> {
        return when {
            Build.VERSION.SDK_INT >= Build.VERSION_CODES.UPSIDE_DOWN_CAKE -> {
                arrayOf(
                    Manifest.permission.READ_MEDIA_IMAGES,
                    Manifest.permission.READ_MEDIA_VIDEO,
                    Manifest.permission.READ_MEDIA_VISUAL_USER_SELECTED
                )
            }

            Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU -> {
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
            Toast.makeText(
                context,
                "Camera permission is required to take pictures",
                Toast.LENGTH_SHORT
            ).show()
        }
        PermissionUtils.requestPermission(
            requireActivity(),
            Manifest.permission.CAMERA,
            PermissionUtils.REQUEST_CAMERA_PERMISSION
        )
    }

    private fun requestGalleryPermission() {
        val storagePermissions = getStoragePermission()
        if (PermissionUtils.shouldShowRationale(requireActivity(), storagePermissions[0])) {
            Toast.makeText(
                context,
                "Storage permission is required to select pictures",
                Toast.LENGTH_SHORT
            ).show()
        }
        PermissionUtils.requestPermission(
            requireActivity(),
            storagePermissions[0],
            PermissionUtils.REQUEST_GALLERY_PERMISSION
        )
    }

    private fun requestLocationPermission() {
        if (PermissionUtils.shouldShowRationale(
                requireActivity(),
                Manifest.permission.ACCESS_FINE_LOCATION
            )
        ) {
            Toast.makeText(
                context,
                "Location permission is required to get current location",
                Toast.LENGTH_SHORT
            ).show()
        }
        PermissionUtils.requestPermission(
            requireActivity(),
            Manifest.permission.ACCESS_FINE_LOCATION,
            PermissionUtils.REQUEST_LOCATION_PERMISSION
        )
    }

    private fun openCamera() {
        val intent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
        val photoFile: File? = try {
            createImageFile()
        } catch (ex: IOException) {
            null
        }

        photoFile?.also {
            val photoURI = FileProvider.getUriForFile(
                requireContext(),
                "${requireContext().packageName}.fileprovider",
                it
            )
            intent.putExtra(MediaStore.EXTRA_OUTPUT, photoURI)
            currentPhotoPath = it.absolutePath
        }

        startActivityForResult(intent, PermissionUtils.REQUEST_CAMERA_PERMISSION)
    }

    private fun openGallery() {
        launcherGallery.launch(PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageAndVideo))
    }

    private fun checkGPSAndFetchLocation() {
        val locationManager =
            requireContext().getSystemService(Context.LOCATION_SERVICE) as LocationManager
        if (!locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
            AlertDialog.Builder(requireContext())
                .setMessage("GPS is disabled. Do you want to enable it?")
                .setCancelable(false)
                .setPositiveButton("Yes") { _, _ ->
                    startActivityForResult(Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS), 100)
                }
                .setNegativeButton("No") { dialog, _ ->
                    dialog.dismiss()
                    Toast.makeText(requireContext(), "GPS is required to fetch location", Toast.LENGTH_SHORT).show()
                }
                .show()
        } else {
            getCurrentLocation()
        }
    }

    private fun getCurrentLocation() {
        if (ActivityCompat.checkSelfPermission(
                requireContext(),
                Manifest.permission.ACCESS_FINE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            requestLocationPermission()
            return
        }

        locationRequest = LocationRequest.Builder(Priority.PRIORITY_HIGH_ACCURACY, 2000)
            .setWaitForAccurateLocation(false)
            .setMinUpdateIntervalMillis(1000)
            .setMaxUpdateDelayMillis(2000)
            .build()

        locationCallback = object : LocationCallback() {
            override fun onLocationResult(locationResult: LocationResult) {
                val context = context ?: return
                for (location in locationResult.locations) {
                    if (location != null) {
                        latitude = location.latitude.toString()
                        longitude = location.longitude.toString()
                        val address = GeocodingUtils.getLocationName(context, latitude.toDouble(), longitude.toDouble())
                        binding.ivRecentLocation.visibility = View.VISIBLE
                        binding.tvLocation.visibility = View.VISIBLE
                        binding.tvLocation.text = address
                        fusedLocationClient.removeLocationUpdates(this)
                        break
                    }
                }
            }
        }

        fusedLocationClient.requestLocationUpdates(locationRequest, locationCallback, null)
    }

    @Throws(IOException::class)
    private fun createImageFile(): File {
        val timeStamp: String = SimpleDateFormat("yyyyMMdd_HHmmss").format(Date())
        val storageDir = File(
            Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES),
            "Farmshields"
        )
        if (!storageDir.exists()) {
            storageDir.mkdirs()
        }
        return File.createTempFile(
            "JPEG_${timeStamp}_",
            ".jpg",
            storageDir
        ).apply {
            currentPhotoPath = absolutePath
        }
    }

    private fun resizeImage(file: File): File {
        val options = BitmapFactory.Options().apply {
            inJustDecodeBounds = true
        }
        BitmapFactory.decodeFile(file.path, options)
        val (width, height) = options.run { outWidth to outHeight }

        val maxSize = 1000
        var scaleFactor = 1

        if (width > height) {
            if (width > maxSize) scaleFactor = width / maxSize
        } else {
            if (height > maxSize) scaleFactor = height / maxSize
        }

        options.inJustDecodeBounds = false
        options.inSampleSize = scaleFactor

        val resizedBitmap = BitmapFactory.decodeFile(file.path, options)

        val resizedFile = createImageFile()
        val outputStream = FileOutputStream(resizedFile)
        resizedBitmap.compress(Bitmap.CompressFormat.JPEG, 85, outputStream)
        outputStream.flush()
        outputStream.close()

        return resizedFile
    }

    @Deprecated("Deprecated in Java")
    override fun onRequestPermissionsResult(
        requestCode: Int, permissions: Array<out String>, grantResults: IntArray
    ) {
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

            PermissionUtils.REQUEST_LOCATION_PERMISSION -> {
                if ((grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED)) {
                    getCurrentLocation()
                } else {
                    Toast.makeText(context, "Location permission denied", Toast.LENGTH_SHORT).show()
                }
            }
        }
    }

    @Deprecated("Deprecated in Java")
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (resultCode == Activity.RESULT_OK) {
            if (requestCode == 100) {
                getCurrentLocation()
            }
            when (requestCode) {
                PermissionUtils.REQUEST_CAMERA_PERMISSION -> {
                    val file = File(currentPhotoPath)
                    val uri = FileProvider.getUriForFile(
                        requireContext(),
                        "com.gagak.farmshields.fileprovider",
                        file
                    )
                    binding.ivPreviewImage.setImageURI(uri)
                    binding.cardViewPreview.visibility = View.VISIBLE
                    // Don't call uploadImage here; call it when user confirms the upload action
                    selectedImageUri = uri
                    val mediaScanIntent = Intent(ACTION_MEDIA_SCANNER_SCAN_FILE)
                    val contentUri = Uri.fromFile(file)
                    mediaScanIntent.data = contentUri
                    requireActivity().sendBroadcast(mediaScanIntent)
                }

                PermissionUtils.REQUEST_GALLERY_PERMISSION -> {
                    val selectedImage: Uri? = data?.data
                    selectedImage?.let {
                        binding.ivPreviewImage.setImageURI(it)
                        binding.cardViewPreview.visibility = View.VISIBLE
                        // Don't call uploadImage here; call it when user confirms the upload action
                        selectedImageUri = it
                    }
                }
            }
        }
    }

    override fun onResume() {
        super.onResume()
        // Register the BroadcastReceiver to listen for location changes
        requireContext().registerReceiver(locationReceiver, IntentFilter(LocationManager.PROVIDERS_CHANGED_ACTION))
    }

    override fun onPause() {
        super.onPause()
        // Unregister the BroadcastReceiver when the fragment is paused
        requireContext().unregisterReceiver(locationReceiver)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
        fusedLocationClient.removeLocationUpdates(locationCallback)
    }
}

