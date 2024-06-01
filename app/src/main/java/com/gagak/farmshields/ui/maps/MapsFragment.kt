package com.gagak.farmshields.ui.maps

import android.Manifest
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.location.LocationManager
import androidx.fragment.app.Fragment
import android.os.Bundle
import android.provider.Settings
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.ProgressBar
import android.widget.TextView
import androidx.appcompat.app.AlertDialog
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.navigation.fragment.findNavController
import com.bumptech.glide.Glide
import com.gagak.farmshields.R
import com.gagak.farmshields.databinding.FragmentMapsBinding
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.card.MaterialCardView

class MapsFragment : Fragment(){

    private lateinit var googleMap: GoogleMap
    private var _binding: FragmentMapsBinding? = null
    private lateinit var bottomSheetBehavior: BottomSheetBehavior<MaterialCardView>
    private val binding get() = _binding!!
    private lateinit var loadingIndicator: ProgressBar

    private val callback = OnMapReadyCallback { googleMap ->
        val sydney = LatLng(-34.0, 151.0)
        googleMap.addMarker(MarkerOptions().position(sydney).title("Marker in Sydney"))
        googleMap.moveCamera(CameraUpdateFactory.newLatLng(sydney))

        loadingIndicator.visibility = View.GONE
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentMapsBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        loadingIndicator = view.findViewById(R.id.mapLoading)
        preloadMap()
//        val mapFragment = childFragmentManager.findFragmentById(R.id.map) as SupportMapFragment?
//        mapFragment?.getMapAsync(this)

        bottomSheetBehavior = BottomSheetBehavior.from(binding.bottomSheetCardView)
        bottomSheetBehavior.state = BottomSheetBehavior.STATE_COLLAPSED
        bottomSheetBehavior.peekHeight = 200

        binding.backButton.setOnClickListener {
            findNavController().navigateUp()
        }

        checkPermissions()
        checkGPS()
    }

    private fun preloadMap() {
        val mapFragment = childFragmentManager.findFragmentById(R.id.map) as SupportMapFragment?
        mapFragment?.getMapAsync(callback)
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
                    startActivity(Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS))
                }
                .show()
        }
    }

    private fun showBottomSheet() {
//        binding.apply {
//            bottomSheet.findViewById<TextView>(R.id.nameDetails).text = story.name
//            bottomSheet.findViewById<TextView>(R.id.locationDetails).text =
//                getLocationName(story.lat, story.lon)
//            bottomSheet.findViewById<TextView>(R.id.descPost).text = story.description
//            val imageView = bottomSheet.findViewById<ImageView>(R.id.imagePreview)
//            val cardView =
//                bottomSheet.findViewById<androidx.cardview.widget.CardView>(R.id.cardView)
//            if (story.photoUrl.isNotEmpty()) {
//                Glide.with(this@MapsFragment).load(story.photoUrl).into(imageView)
//                imageView.visibility = View.VISIBLE
//                cardView.visibility = View.VISIBLE
//            } else {
//                imageView.visibility = View.GONE
//                cardView.visibility = View.GONE
//            }
//        }
//        selectedStory = story
        bottomSheetBehavior.state = BottomSheetBehavior.STATE_EXPANDED
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    companion object {
        private const val LOCATION_PERMISSION_REQUEST_CODE = 1
    }
}
