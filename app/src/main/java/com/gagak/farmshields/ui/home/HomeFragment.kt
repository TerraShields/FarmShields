package com.gagak.farmshields.ui.home

import android.Manifest
import android.animation.Animator
import android.animation.ObjectAnimator
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.location.Geocoder
import android.location.LocationManager
import android.os.Bundle
import android.provider.Settings
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.AccelerateDecelerateInterpolator
import android.widget.Toast
import androidx.activity.OnBackPressedCallback
import androidx.appcompat.app.AlertDialog
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.bumptech.glide.Glide
import com.gagak.farmshields.R
import com.gagak.farmshields.core.data.adapter.MainAdapter
import com.gagak.farmshields.core.data.local.preferences.AuthPreferences
import com.gagak.farmshields.core.domain.model.viewmodel.main.MainViewModel
import com.gagak.farmshields.core.domain.model.viewmodel.user.UserViewModel
import com.gagak.farmshields.databinding.FragmentHomeBinding
import com.google.android.gms.location.*
import org.koin.androidx.viewmodel.ext.android.viewModel
import java.util.*

class HomeFragment : Fragment() {

    private val viewModel: UserViewModel by viewModel()
    private val mainViewModel: MainViewModel by viewModel()
    private var _binding: FragmentHomeBinding? = null
    private val binding get() = _binding!!
    private lateinit var authPreferences: AuthPreferences
    private lateinit var mainAdapter: MainAdapter
    private lateinit var fusedLocationClient: FusedLocationProviderClient
    private lateinit var locationRequest: LocationRequest
    private var locationCallback: LocationCallback? = null
    private var latitude: String = ""
    private var longitude: String = ""

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentHomeBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        authPreferences = AuthPreferences(requireContext())
        checkAuthentication()

        viewModel.getUser()

        viewModel.userResponse.observe(viewLifecycleOwner, Observer { response ->
            response?.data?.user?.let { userDetail ->
                binding.apply {
                    Glide.with(this@HomeFragment)
                        .load(userDetail.image)
                        .into(logo)
                }
            }
        })

        mainAdapter = MainAdapter(requireContext())
        binding.recyclerViewReports.apply {
            layoutManager = LinearLayoutManager(context)
            adapter = mainAdapter
        }

        mainViewModel.getReports().observe(viewLifecycleOwner, Observer { pagingData ->
            mainAdapter.submitData(lifecycle, pagingData)
        })

        mainViewModel.getReport(1, 10, 0).observe(viewLifecycleOwner, Observer { response ->
            if (response.isSuccessful) {
                val reports = response.body()?.data
                // handle reports
            } else {
                Log.e("HomeFragment", "Error fetching reports: ${response.errorBody()?.string()}")
            }
        })

        viewModel.error.observe(viewLifecycleOwner, Observer { error ->
            Log.e("HomeFragment", "Error fetching user data: $error")
        })

        fusedLocationClient = LocationServices.getFusedLocationProviderClient(requireActivity())
        checkGPSAndFetchLocation()

        requireActivity().onBackPressedDispatcher.addCallback(
            viewLifecycleOwner,
            object : OnBackPressedCallback(true) {
                override fun handleOnBackPressed() {
                    requireActivity().finishAffinity()
                }
            }
        )

        binding.apply {
            logo.setOnClickListener { navigateToProfile() }
            ivAnita.setOnClickListener { navigateToAnita() }
            askFarmer.setOnClickListener { navigateToAnita() }
            reportBug.setOnClickListener { navigateToReportBug() }
        }
    }

    private fun checkAuthentication() {
        val token = authPreferences.getToken()
        if (authPreferences.getToken().isNullOrEmpty()) {
            Log.d("Authentication", "Token: $token")
            navigateToLogin()
        }
    }

    private fun checkGPSAndFetchLocation() {
        val locationManager =
            requireContext().getSystemService(Context.LOCATION_SERVICE) as LocationManager
        if (!locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
            AlertDialog.Builder(requireContext())
                .setMessage("GPS is disabled. Do you want to enable it?")
                .setCancelable(false)
                .setPositiveButton("Yes") { _, _ ->
                    startActivityForResult(Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS), LOCATION_REQUEST_CODE)
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

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == LOCATION_REQUEST_CODE) {
            val locationManager =
                requireContext().getSystemService(Context.LOCATION_SERVICE) as LocationManager
            if (locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
                getCurrentLocation()
            } else {
                Toast.makeText(requireContext(), "GPS is required to fetch location", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun getCurrentLocation() {
        if (ActivityCompat.checkSelfPermission(
                requireContext(),
                Manifest.permission.ACCESS_FINE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            ActivityCompat.requestPermissions(
                requireActivity(),
                arrayOf(Manifest.permission.ACCESS_FINE_LOCATION),
                LOCATION_PERMISSION_REQUEST_CODE
            )
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
                        val address = getAddressFromLatLng(context, latitude.toDouble(), longitude.toDouble())
                        animateLocationStatus(address)
                        fusedLocationClient.removeLocationUpdates(this)
                        break
                    }
                }
            }
        }

        fusedLocationClient.requestLocationUpdates(locationRequest, locationCallback!!, null)
    }

    private fun getAddressFromLatLng(context: Context, latitude: Double, longitude: Double): String {
        val geocoder = Geocoder(context, Locale.getDefault())
        val addresses = geocoder.getFromLocation(latitude, longitude, 1)
        return if (addresses!!.isNotEmpty()) {
            addresses[0].getAddressLine(0)
        } else {
            "Unknown location"
        }
    }

    private fun animateLocationStatus(address: String) {
        binding.locationStatus.apply {
            visibility = View.VISIBLE
            text = "Wilayah Anda Aman"
        }

        // Animate the slide down effect
        val slideDown = ObjectAnimator.ofFloat(binding.locationStatus, "translationY", -100f, 0f)
        slideDown.duration = 1000
        slideDown.interpolator = AccelerateDecelerateInterpolator()

        // Animate the text change and color change based on address
        slideDown.addListener(object : Animator.AnimatorListener {
            override fun onAnimationStart(animation: Animator) {}
            override fun onAnimationEnd(animation: Animator) {
                binding.locationStatus.text = address
                // Update the background color based on the address (simulating the report sign)
                binding.locationStatus.setBackgroundColor(
                    when {
                        address.contains("bahaya", true) -> ContextCompat.getColor(requireContext(), R.color.dangerSign)
                        address.contains("peringatan", true) -> ContextCompat.getColor(requireContext(), R.color.warningnSign)
                        else -> ContextCompat.getColor(requireContext(), R.color.safeContainer)
                    }
                )
            }

            override fun onAnimationCancel(animation: Animator) {}
            override fun onAnimationRepeat(animation: Animator) {}
        })

        slideDown.start()
    }

    private fun navigateToAnita() {
        findNavController().navigate((R.id.action_homeFragment_to_anitaFragment))
    }

    private fun navigateToReportBug() {
        findNavController().navigate(R.id.action_homeFragment_to_reportFragment)
    }

    private fun navigateToProfile() {
        findNavController().navigate(R.id.action_homeFragment_to_profileFragment)
    }

    private fun navigateToLogin() {
        findNavController().navigate(R.id.action_homeFragment_to_loginFragment)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
        locationCallback?.let { fusedLocationClient.removeLocationUpdates(it) }
    }

    companion object {
        private const val LOCATION_PERMISSION_REQUEST_CODE = 1
        private const val LOCATION_REQUEST_CODE = 2
    }
}
