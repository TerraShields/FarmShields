package com.gagak.farmshields.ui.home

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.bumptech.glide.Glide
import com.gagak.farmshields.R
import com.gagak.farmshields.core.utils.Date
import com.gagak.farmshields.core.utils.GeocodingUtils
import com.gagak.farmshields.databinding.FragmentHomeDetailsBinding
import com.google.android.material.appbar.AppBarLayout

class HomeDetailsFragment : Fragment() {
    private var _binding: FragmentHomeDetailsBinding? = null
    private val binding get() = _binding!!
    private val args: HomeDetailsFragmentArgs by navArgs()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentHomeDetailsBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val report = args.report

        binding.apply {
            placeTitle.text = report.classificationResult
            pestName.text = report.reportId
            placeLocation.text = GeocodingUtils.getLocationName(requireContext(), report.location.latitude.toDouble(), report.location.longitude.toDouble())
            placeDate.text = "Tanggal: ${Date.formatTimeDifference(Date.parseIso8601(report.createdAt), System.currentTimeMillis())}"
            placeDetails.text = report.description
            Glide.with(this@HomeDetailsFragment).load(report.image).into(backgroundImage)

            backButton.setOnClickListener {
                findNavController().navigate(R.id.homeFragment)
            }

            // Listen to the AppBarLayout offset changes
            appBarLayout.addOnOffsetChangedListener(AppBarLayout.OnOffsetChangedListener { appBarLayout, verticalOffset ->
                val totalScrollRange = appBarLayout.totalScrollRange
                if (totalScrollRange + verticalOffset == 0) {
                    // Collapsed
                    activity?.window?.decorView?.systemUiVisibility = View.SYSTEM_UI_FLAG_VISIBLE
                    activity?.window?.statusBarColor = ContextCompat.getColor(requireContext(), R.color.colorSecondary)
                } else {
                    // Expanded
                    activity?.window?.decorView?.systemUiVisibility = (
                            View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                                    or View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                            )
                    activity?.window?.statusBarColor = android.graphics.Color.TRANSPARENT
                }
            })
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}