package com.gagak.farmshields.ui.auth

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ProgressBar
import android.widget.Toast
import androidx.activity.OnBackPressedCallback
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.navigation.fragment.findNavController
import com.gagak.farmshields.R
import com.gagak.farmshields.core.data.local.preferences.AuthPreferences
import com.gagak.farmshields.core.domain.model.viewmodel.auth.AuthViewModel
import com.gagak.farmshields.databinding.FragmentLoginBinding
import com.gagak.farmshields.ui.customeview.EmailCustomeView
import com.gagak.farmshields.ui.customeview.PasswordCustomeView
import com.google.android.material.textfield.TextInputEditText
import org.koin.androidx.viewmodel.ext.android.viewModel

class LoginFragment : Fragment() {
    private val authViewModel: AuthViewModel by viewModel()
    private var _binding: FragmentLoginBinding? = null
    private val binding get() = _binding!!
    private lateinit var emailCustomView: EmailCustomeView
    private lateinit var passwordCustomView: PasswordCustomeView
    private lateinit var loginButton: Button
    private lateinit var progressBar: ProgressBar
    private lateinit var registerButton: Button

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentLoginBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        emailCustomView = binding.emailCustomView
        passwordCustomView = binding.passwordCustomView
        loginButton = binding.loginButton
        registerButton = binding.createAccountButton
        progressBar = binding.progressBar

        loginButton.setOnClickListener {
            val email = emailCustomView.findViewById<TextInputEditText>(R.id.username_input).text.toString()
            val password = passwordCustomView.findViewById<TextInputEditText>(R.id.password_input).text.toString()
            authViewModel.login(email, password)
        }

        authViewModel.loginResponse.observe(viewLifecycleOwner, Observer { success ->
            if (success) {
                val token = authViewModel.authToken.value
                if (token != null) {
                    Log.d("Authentication", "Token: $token")
                    val authPreferences = AuthPreferences(requireContext())
                    authPreferences.saveToken(token)
                    navigateToHome()
                }
                Toast.makeText(context, "Login Successful", Toast.LENGTH_SHORT).show()
            } else {
                Toast.makeText(context, "Login Failed", Toast.LENGTH_SHORT).show()
            }
        })

        authViewModel.loading.observe(viewLifecycleOwner, Observer { isLoading ->
            progressBar.visibility = if (isLoading) View.VISIBLE else View.GONE
        })

        authViewModel.error.observe(viewLifecycleOwner, Observer { error ->
            error?.let {
                Toast.makeText(context, it, Toast.LENGTH_SHORT).show()
            }
        })

        registerButton.setOnClickListener {
            navigateToRegister()
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

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    private fun navigateToHome() {
        findNavController().navigate(R.id.action_loginFragment_to_homeFragment)
    }

    private fun navigateToRegister() {
        findNavController().navigate(R.id.action_loginFragment_to_registerFragment)
    }
}
