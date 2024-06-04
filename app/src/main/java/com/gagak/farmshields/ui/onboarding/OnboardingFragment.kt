package com.gagak.farmshields.ui.onboarding

import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import androidx.viewpager2.widget.ViewPager2
import com.gagak.farmshields.R
import com.gagak.farmshields.databinding.FragmentOnboardingBinding
import com.google.android.material.tabs.TabLayout
import com.google.android.material.tabs.TabLayoutMediator

class OnboardingFragment : Fragment() {

    private var _binding: FragmentOnboardingBinding? = null
    private val binding get() = _binding!!
    private lateinit var handler: Handler
    private lateinit var runnable: Runnable
    private val slideInterval = 3000L

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentOnboardingBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val fragmentList = arrayListOf<Fragment>(
            OnboardingScreenFragment.newInstance("Selamat Datang Di Hapetani", "Hapetani adalah aplikasi yang membantu petani untuk mengenali dan menanggulangi hama pada tanaman mereka dengan cepat dan mudah. Dengan Hapetani", R.drawable.logo_onboard1),
            OnboardingScreenFragment.newInstance("Deteksi dini penyebaran hama", "Proyek kami dapat memprediksi penyebaran hama secara real-time hari demi hari.", R.drawable.logo_onboard2),
            OnboardingScreenFragment.newInstance("Ayo Mulai Dengan Hapetani", "Lindungi panenmu, tingkatkan hasil panenmu dengan Hapetani!", R.drawable.logo_onboard3)
        )

        binding.apply {
            buttonSignIn.setOnClickListener{
                navigateToLogin()
            }
        }

        val adapter = ViewPagerAdapter(fragmentList, requireActivity().supportFragmentManager, lifecycle)
        binding.viewPager.adapter = adapter

        val dotsIndicator = binding.indicator
        dotsIndicator.setViewPager2(binding.viewPager)

        handler = Handler(Looper.getMainLooper())
        runnable = object : Runnable {
            override fun run() {
                val currentItem = binding.viewPager.currentItem
                val nextItem = if (currentItem == fragmentList.size - 1) 0 else currentItem + 1
                binding.viewPager.setCurrentItem(nextItem, true)
                handler.postDelayed(this, slideInterval)
            }
        }

        handler.postDelayed(runnable, slideInterval)

    }

    private fun navigateToLogin(){
        findNavController().navigate(R.id.action_onboardingFragment_to_loginFragment)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        handler.removeCallbacks(runnable)
        _binding = null
    }
}
