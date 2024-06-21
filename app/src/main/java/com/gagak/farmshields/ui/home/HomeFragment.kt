package com.gagak.farmshields.ui.home

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.OnBackPressedCallback
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
import org.koin.androidx.viewmodel.ext.android.viewModel

class HomeFragment : Fragment() {

    private val viewModel: UserViewModel by viewModel()
    private val mainViewModel: MainViewModel by viewModel()
    private var _binding: FragmentHomeBinding? = null
    private val binding get() = _binding!!
    private lateinit var authPreferences: AuthPreferences
    private lateinit var mainAdapter: MainAdapter

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

        binding.apply {
            logo.setOnClickListener {
                navigateToProfile()
            }
            ivAnita.setOnClickListener {
                navigateToAnita()
            }
            askFarmer.setOnClickListener {
                navigateToAnita()
            }
            reportBug.setOnClickListener {
                navigateToReportBug()
            }
        }

        requireActivity().onBackPressedDispatcher.addCallback(
            viewLifecycleOwner,
            object : OnBackPressedCallback(true) {
                override fun handleOnBackPressed() {
                    requireActivity().finishAffinity()
                }
            }
        )
    }

    private fun checkAuthentication() {
        val token = authPreferences.getToken()
        if (authPreferences.getToken().isNullOrEmpty()) {
            Log.d("Authentication", "Token: $token")
            navigateToLogin()
        }
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
    }
}
