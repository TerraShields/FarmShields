package com.gagak.farmshields

import android.os.Build
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.WindowInsets
import android.view.WindowInsetsController
import androidx.annotation.RequiresApi
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.fragment.app.Fragment
import androidx.navigation.NavOptions
import androidx.navigation.fragment.findNavController
import com.gagak.farmshields.databinding.FragmentSplashBinding

class SplashFragment : Fragment() {

    private var _binding: FragmentSplashBinding? = null
    private val binding get() = _binding!!

    @RequiresApi(Build.VERSION_CODES.R)
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentSplashBinding.inflate(inflater, container, false)
        // Set full screen
        requireActivity().window.insetsController?.apply {
            hide(WindowInsets.Type.statusBars() or WindowInsets.Type.navigationBars())
            systemBarsBehavior = WindowInsetsController.BEHAVIOR_SHOW_TRANSIENT_BARS_BY_SWIPE
        }
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        Log.d("SplashFragment", "SplashFragment initialized")

        Handler(Looper.getMainLooper()).postDelayed({
            navigateToOnboard()
        }, 3000) // 3-second delay
    }

    private fun navigateToOnboard() {
        val navController = findNavController()
        val currentDestination = navController.currentDestination
        val currentDestinationId = currentDestination?.id

        Log.d("SplashFragment", "Current Destination: $currentDestinationId (${currentDestination?.label})")

        if (currentDestinationId == R.id.splashFragment) {
            navController.navigate(
                R.id.action_splashFragment_to_onboardingFragment,
                null,
                NavOptions.Builder().setPopUpTo(R.id.splashFragment, true).build()
            )
        } else {
            Log.e("SplashFragment", "Unexpected current destination: ${currentDestination?.label}")
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null

        // Reset full screen setting when exiting the SplashFragment
        val windowInsetsController = ViewCompat.getWindowInsetsController(requireActivity().window.decorView)
        windowInsetsController?.show(WindowInsetsCompat.Type.statusBars() or WindowInsetsCompat.Type.navigationBars())
    }
}
