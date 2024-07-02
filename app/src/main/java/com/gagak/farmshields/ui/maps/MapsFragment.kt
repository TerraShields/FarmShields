package com.gagak.farmshields.ui.maps

import android.Manifest
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.location.Geocoder
import android.location.Location
import android.location.LocationManager
import android.os.Build
import android.os.Bundle
import android.provider.Settings
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.WindowInsets
import android.view.WindowInsetsController
import android.view.WindowManager
import android.widget.ImageView
import android.widget.ProgressBar
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.bumptech.glide.Glide
import com.gagak.farmshields.R
import com.gagak.farmshields.core.domain.model.main.ReportModel
import com.gagak.farmshields.core.domain.model.viewmodel.main.MainViewModel
import com.gagak.farmshields.databinding.FragmentMapsBinding
import com.google.android.gms.location.*
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.BitmapDescriptorFactory
import com.google.android.gms.maps.model.CircleOptions
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.LatLngBounds
import com.google.android.gms.maps.model.MarkerOptions
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.card.MaterialCardView
import org.koin.androidx.viewmodel.ext.android.viewModel
import java.util.*

class MapsFragment : Fragment(), OnMapReadyCallback {

    private lateinit var googleMap: GoogleMap
    private var _binding: FragmentMapsBinding? = null
    private lateinit var bottomSheetBehavior: BottomSheetBehavior<MaterialCardView>
    private val binding get() = _binding!!
    private lateinit var loadingIndicator: ProgressBar
    private val mainViewModel: MainViewModel by viewModel()
    private lateinit var fusedLocationClient: FusedLocationProviderClient
    private lateinit var locationRequest: LocationRequest
    private var locationCallback: LocationCallback? = null
    private var currentLocation: Location? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentMapsBinding.inflate(inflater, container, false)
        makeStatusBarTransparent()
        return binding.root
    }

    private fun makeStatusBarTransparent() {
        val window = requireActivity().window
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            window.setDecorFitsSystemWindows(false)
            window.insetsController?.apply {
                show(WindowInsets.Type.statusBars())
                systemBarsBehavior = WindowInsetsController.BEHAVIOR_SHOW_TRANSIENT_BARS_BY_SWIPE
            }
        } else {
            @Suppress("DEPRECATION")
            window.decorView.systemUiVisibility = (
                    View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                            or View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                    )
            @Suppress("DEPRECATION")
            window.addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS)
        }
        window.statusBarColor = ContextCompat.getColor(requireContext(), android.R.color.transparent)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(requireActivity())
        loadingIndicator = view.findViewById(R.id.mapLoading)
        preloadMap()
        bottomSheetBehavior = BottomSheetBehavior.from(binding.bottomSheetCardView)
        bottomSheetBehavior.state = BottomSheetBehavior.STATE_COLLAPSED
        bottomSheetBehavior.peekHeight = 200
        binding.apply {
            backButton.setOnClickListener { findNavController().navigateUp() }
            currentLocationButton.setOnClickListener {
                checkPermissions()
                checkGPSAndFetchLocation()
            }
        }
        checkPermissions()
        checkGPS()
        setupObservers()
    }

    private fun preloadMap() {
        val mapFragment = childFragmentManager.findFragmentById(R.id.map) as SupportMapFragment?
        mapFragment?.getMapAsync(this)
    }

    private fun checkPermissions() {
        if (ContextCompat.checkSelfPermission(
                requireContext(),
                Manifest.permission.ACCESS_FINE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            ActivityCompat.requestPermissions(
                requireActivity(),
                arrayOf(Manifest.permission.ACCESS_FINE_LOCATION),
                LOCATION_PERMISSION_REQUEST_CODE
            )
        }
    }

    private fun checkGPS() {
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

    private fun setupObservers() {
        mainViewModel.getReport(1, 10, 1).observe(viewLifecycleOwner, { response ->
            if (response.isSuccessful) {
                response.body()?.data?.let { reports ->
                    val boundsBuilder = LatLngBounds.Builder()
                    var hasIncludedPoints = false // Track if points have been included

                    for (report in reports) {
                        val latLng = LatLng(report.location.latitude.toDouble(), report.location.longitude.toDouble())
                        val (markerColor, circleColor) = when (report.sign.lowercase()) {
                            "bahaya" -> Pair(BitmapDescriptorFactory.HUE_RED, 0x22FF0000)
                            "peringatan" -> Pair(BitmapDescriptorFactory.HUE_YELLOW, 0x22FFFF00)
                            "aman" -> Pair(BitmapDescriptorFactory.HUE_GREEN, 0x2200FF00)
                            else -> Pair(BitmapDescriptorFactory.HUE_GREEN, 0x2200FF00)
                        }

                        val marker = googleMap.addMarker(
                            MarkerOptions().position(latLng)
                                .title(getAddressFromLatLng(requireContext(), latLng.latitude, latLng.longitude))
                                .icon(BitmapDescriptorFactory.defaultMarker(markerColor))
                        )
                        marker?.tag = report
                        boundsBuilder.include(latLng)
                        hasIncludedPoints = true // Mark that a point has been included

                        googleMap.addCircle(
                            CircleOptions()
                                .center(latLng)
                                .radius(1000.0) // Radius in meters
                                .strokeColor(circleColor)
                                .fillColor(circleColor)
                        )
                    }

                    // Adjust the camera to fit all markers if there are included points
                    if (hasIncludedPoints) {
                        val bounds = boundsBuilder.build()
                        googleMap.moveCamera(CameraUpdateFactory.newLatLngBounds(bounds, 100))
                    }
                }
            }
            loadingIndicator.visibility = View.GONE
        })
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

    override fun onMapReady(map: GoogleMap) {
        googleMap = map
        googleMap.setOnMarkerClickListener { marker ->
            val report = marker.tag as? ReportModel
            report?.let { showBottomSheet(it) }
            true
        }
    }

    private fun showBottomSheet(report: ReportModel) {
        binding.apply {
            nameDetails.text = report.userId
            locationDetails.text = "${report.location.latitude}, ${report.location.longitude}"
            descPost.text = report.description
            if (report.image.isNotEmpty()) {
                Glide.with(this@MapsFragment).load(report.image).into(imagePreview)
                imagePreview.visibility = View.VISIBLE
                cardView.visibility = View.VISIBLE
            } else {
                imagePreview.visibility = View.GONE
                cardView.visibility = View.GONE
            }

            // Display wind direction prediction
            windDirectionContainer.removeAllViews()
            report.prediction.let {
                if (it.n > 0) addWindDirectionImage(R.drawable.ic_wind_n)
                if (it.ne > 0) addWindDirectionImage(R.drawable.ic_wind_ne)
                if (it.e > 0) addWindDirectionImage(R.drawable.ic_wind_e)
                if (it.se > 0) addWindDirectionImage(R.drawable.ic_wind_se)
                if (it.s > 0) addWindDirectionImage(R.drawable.ic_wind_s)
                if (it.sw > 0) addWindDirectionImage(R.drawable.ic_wind_sw)
                if (it.w > 0) addWindDirectionImage(R.drawable.ic_wind_w)
                if (it.nw > 0) addWindDirectionImage(R.drawable.ic_wind_nw)
            }
        }
        bottomSheetBehavior.state = BottomSheetBehavior.STATE_EXPANDED
    }

    private fun addWindDirectionImage(drawableResId: Int) {
        val imageView = ImageView(context)
        imageView.setImageResource(drawableResId)
        binding.windDirectionContainer.addView(imageView)
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
                        currentLocation = location
                        if (::googleMap.isInitialized) {
                            animateToCurrentLocation()
                        }
                        fusedLocationClient.removeLocationUpdates(this)
                        break
                    }
                }
            }
        }

        fusedLocationClient.requestLocationUpdates(locationRequest, locationCallback!!, null)
    }

    private fun animateToCurrentLocation() {
        currentLocation?.let {
            val latLng = LatLng(it.latitude, it.longitude)
            googleMap.animateCamera(CameraUpdateFactory.newLatLngZoom(latLng, 15f))
        }
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
