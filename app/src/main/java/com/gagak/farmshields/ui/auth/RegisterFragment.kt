package com.gagak.farmshields.ui.auth

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.gagak.farmshields.R
import com.gagak.farmshields.core.domain.model.auth.AuthModel
import com.gagak.farmshields.core.domain.model.viewmodel.auth.AuthViewModel
import com.gagak.farmshields.databinding.FragmentRegisterBinding
import com.gagak.farmshields.ui.customeview.ConfirmPasswordCustomeView
import com.gagak.farmshields.ui.customeview.EmailCustomeView
import com.gagak.farmshields.ui.customeview.NameCustomeView
import com.gagak.farmshields.ui.customeview.PasswordCustomeView
import org.koin.androidx.viewmodel.ext.android.viewModel

class RegisterFragment : Fragment() {
    private val authViewModel: AuthViewModel by viewModel()
    private var _binding: FragmentRegisterBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentRegisterBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.apply {
            registerButton.setOnClickListener {
                val name = nameCustomView.getText()
                val email = emailCustomView.getText()
                val password = passwordCustomView.getText()
                val confirmPassword = confirmPasswordCustomView.getText()

                if (password == confirmPassword) {
                    val authModel = AuthModel(
                        name = name,
                        email = email,
                        password = password,
                        userId = "",
                        passwordConfirmation = confirmPassword
                    )
                    authViewModel.register(authModel)
                } else {
                    Toast.makeText(context, getString(R.string.password_mismatch_error), Toast.LENGTH_SHORT).show()
                }
            }

            login.setOnClickListener {
                navigateToLogin()
            }
        }

        authViewModel.registerResponse.observe(viewLifecycleOwner) {
            it?.let { response ->
                Toast.makeText(context, response.message, Toast.LENGTH_SHORT).show()
                findNavController().navigate(R.id.action_registerFragment_to_loginFragment)
            }
        }

        authViewModel.error.observe(viewLifecycleOwner) {
            it?.let { error ->
                Toast.makeText(context, error, Toast.LENGTH_SHORT).show()
            }
        }

        authViewModel.loading.observe(viewLifecycleOwner) { isLoading ->
            binding.progressBar.visibility = if (isLoading) View.VISIBLE else View.GONE
        }
    }

    private fun navigateToLogin() {
        findNavController().navigate(R.id.action_registerFragment_to_loginFragment)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
