package com.gagak.farmshields.ui.profile

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.OnBackPressedCallback
import androidx.lifecycle.Observer
import androidx.navigation.NavOptions
import androidx.navigation.fragment.findNavController
import com.bumptech.glide.Glide
import com.gagak.farmshields.R
import com.gagak.farmshields.core.data.local.preferences.AuthPreferences
import com.gagak.farmshields.core.domain.model.viewmodel.user.UserViewModel
import com.gagak.farmshields.databinding.FragmentProfileBinding
import org.koin.androidx.viewmodel.ext.android.viewModel

class ProfileFragment : Fragment() {
    private val viewModel: UserViewModel by viewModel()
    private var _binding: FragmentProfileBinding? = null
    private val binding get() = _binding!!
    private lateinit var authPreferences: AuthPreferences

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        _binding = FragmentProfileBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        authPreferences = AuthPreferences(requireContext())

        viewModel.getUser()

        viewModel.userResponse.observe(viewLifecycleOwner, Observer { response ->
            response?.data?.user?.let { userDetail ->
                binding.apply {
                    profileName.text = userDetail.name
                    email.text = userDetail.email
                    Glide.with(this@ProfileFragment)
                        .load(userDetail.image)
                        .into(profileImage)
                }
            }
        })

        viewModel.error.observe(viewLifecycleOwner, Observer { error ->
            Log.e("ProfileFragment", "Error fetching user data: $error")
        })

        // Handle logout button click
        binding.apply {
            myAccount.setOnClickListener {
                findNavController().navigate(R.id.action_profileFragment_to_profileUpdateFragment)
            }
            ibLogout.setOnClickListener {
                logout()
            }
            tvLogout.setOnClickListener {
                logout()
            }
            ivBack.setOnClickListener {
                home()
            }
        }

        // Handle back navigation
        requireActivity().onBackPressedDispatcher.addCallback(
            viewLifecycleOwner,
            object : OnBackPressedCallback(true) {
                override fun handleOnBackPressed() {
                    // If the user is not authenticated, block the back navigation
                    if (authPreferences.getToken().isNullOrEmpty()) {
                        findNavController().navigate(R.id.loginFragment)
                    } else {
                        isEnabled = false
                        requireActivity().onBackPressed()
                    }
                }
            }
        )
    }

    private fun logout() {
        // Clear user data (e.g., AuthPreferences)
        authPreferences.clearToken()

        // Create NavOptions to clear the back stack
        val navOptions = NavOptions.Builder()
            .setPopUpTo(R.id.nav_graph, true)
            .build()

        // Navigate back to login screen and clear the back stack
        findNavController().navigate(R.id.action_profileFragment_to_loginFragment, null, navOptions)
    }

    private fun home() {
        // Navigate back to home screen
        findNavController().navigate(R.id.action_profileFragment_to_homeFragment, null)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
