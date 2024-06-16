// HomeFragment.kt
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
        // Inflate the layout for this fragment
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
//                        .placeholder(R.drawable.placeholder_image) // Add a placeholder image
//                        .error(R.drawable.error_image) // Add an error image
                        .into(logo)
                }
            }
        })

        mainAdapter = MainAdapter()
        binding.recyclerViewReports.apply {
            layoutManager = LinearLayoutManager(context)
            adapter = mainAdapter
        }

        mainViewModel.getReports().observe(viewLifecycleOwner, Observer { pagingData ->
            mainAdapter.submitData(lifecycle, pagingData)
        })

        viewModel.error.observe(viewLifecycleOwner, Observer { error ->
            Log.e("HomeFragment", "Error fetching user data: $error")
        })

        binding.apply {
            logo.setOnClickListener {
                navigateToProfile()
            }
<<<<<<< HEAD
            ivAnita.setOnClickListener {
                navigateToAnita()
            }
            askFarmer.setOnClickListener {
                navigateToAnita()
            }

=======
            reportBug.setOnClickListener{
                navigateToReportBug()
            }
>>>>>>> development-authenthication
        }

        // Handle back navigation to exit the app
        requireActivity().onBackPressedDispatcher.addCallback(
            viewLifecycleOwner,
            object : OnBackPressedCallback(true) {
                override fun handleOnBackPressed() {
                    requireActivity().finishAffinity() // Exit the app
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

<<<<<<< HEAD
    private fun navigateToAnita(){
        findNavController().navigate((R.id.action_homeFragment_to_anitaFragment))
    }

    private fun navigateToProfile(){
=======
    private fun navigateToReportBug() {
        findNavController().navigate(R.id.action_homeFragment_to_reportBugFragment)
    }

    private fun navigateToProfile() {
>>>>>>> development-authenthication
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
