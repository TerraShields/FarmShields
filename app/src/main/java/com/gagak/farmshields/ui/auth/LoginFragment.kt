package com.gagak.farmshields.ui.auth

import android.content.Intent
import android.content.IntentSender
import android.graphics.Bitmap
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.webkit.WebView
import android.widget.Button
import android.widget.ProgressBar
import android.widget.Toast
import androidx.activity.OnBackPressedCallback
import androidx.annotation.RequiresApi
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.gagak.farmshields.R
import com.gagak.farmshields.core.data.local.preferences.AuthPreferences
import com.gagak.farmshields.core.domain.model.auth.TokenData
import com.gagak.farmshields.core.domain.model.viewmodel.auth.AuthViewModel
import com.gagak.farmshields.databinding.FragmentLoginBinding
import com.gagak.farmshields.ui.customeview.EmailCustomeView
import com.gagak.farmshields.ui.customeview.PasswordCustomeView
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.material.textfield.TextInputEditText
import com.google.android.gms.common.api.ApiException
import org.koin.androidx.viewmodel.ext.android.viewModel
import com.google.android.gms.auth.api.identity.BeginSignInRequest
import com.google.android.gms.auth.api.identity.Identity
import com.google.android.gms.auth.api.identity.SignInClient
import kotlinx.coroutines.launch
import java.security.SecureRandom
import java.util.Base64

class LoginFragment : Fragment() {
    private val authViewModel: AuthViewModel by viewModel()
    private var _binding: FragmentLoginBinding? = null
    private val binding get() = _binding!!
    private lateinit var emailCustomView: EmailCustomeView
    private lateinit var passwordCustomView: PasswordCustomeView
    private lateinit var loginButton: Button
    private lateinit var progressBar: ProgressBar
    private lateinit var registerButton: Button
    private lateinit var googleSignInClient: GoogleSignInClient
    private lateinit var oneTapClient: SignInClient
    private lateinit var webView: WebView
    private val RC_SIGN_IN = 9001

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentLoginBinding.inflate(inflater, container, false)
        return binding.root
    }

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        emailCustomView = binding.emailCustomView
        passwordCustomView = binding.passwordCustomView
        loginButton = binding.loginButton
        registerButton = binding.createAccountButton
        progressBar = binding.progressBar

//        setupGoogleSignIn()

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

        binding.apply {
            registerButton.setOnClickListener {
                navigateToRegister()
            }
//
//            cardGoogleLogin.setOnClickListener {
//                signInWithGoogle()
//            }
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

//    private fun setupGoogleSignIn() {
//        val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
//            .requestIdToken("534488982667-hi5cp07l3fciuak57hkep7ndj2jmti2s.apps.googleusercontent.com")
//            .requestEmail()
//            .build()
//        googleSignInClient = GoogleSignIn.getClient(requireActivity(), gso)
//        oneTapClient = Identity.getSignInClient(requireActivity())
//    }
//
//
//    @RequiresApi(Build.VERSION_CODES.O)
//    private fun signInWithGoogle() {
//        val beginSignInRequest = BeginSignInRequest.builder()
//            .setGoogleIdTokenRequestOptions(
//                BeginSignInRequest.GoogleIdTokenRequestOptions.builder()
//                    .setSupported(true)
//                    .setServerClientId("534488982667-ml9kkl1bkh709ocgns2nsvc4ar8mfe6f.apps.googleusercontent.com")
//                    .setNonce(generateNonce())
//                    .build()
//            )
//            .build()
//
//        oneTapClient.beginSignIn(beginSignInRequest)
//            .addOnSuccessListener { result ->
//                try {
//                    startIntentSenderForResult(
//                        result.pendingIntent.intentSender, RC_SIGN_IN,
//                        null, 0, 0, 0, null
//                    )
//                } catch (e: IntentSender.SendIntentException) {
//                    Log.e("GoogleSignIn", "Couldn't start One Tap UI: ${e.localizedMessage}")
//                }
//            }
//            .addOnFailureListener { e ->
//                Log.e("GoogleSignIn", "Google Sign-In failed", e)
//            }
//    }
//
//
//    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
//        super.onActivityResult(requestCode, resultCode, data)
//        if (requestCode == RC_SIGN_IN) {
//            try {
//                val credential = oneTapClient.getSignInCredentialFromIntent(data)
//                val idToken = credential.googleIdToken
//                if (idToken != null) {
//                    Log.d("GoogleSignIn", "$idToken")
//                    val authPreferences = AuthPreferences(requireContext())
//                    authPreferences.saveToken(idToken)
//                    authenticateWithServer(idToken)
//                } else {
//                    Log.e("GoogleSignIn", "No ID token!")
//                }
//            } catch (e: ApiException) {
//                Log.e("GoogleSignIn", "Google sign in failed", e)
//            }
//        }
//    }
//
//
//    private fun authenticateWithServer(token: String) {
//        authViewModel.loginWithGoogle(token).observe(viewLifecycleOwner, Observer { success ->
//            if (success) {
//                val token = authViewModel.authToken.value
//                if (token != null) {
//                    Log.d("Authentication", "Token: $token")
//                    val authPreferences = AuthPreferences(requireContext())
//                    authPreferences.saveToken(token)
//                    navigateToHome()
//                } else {
//                    Log.e("Authentication", "Token is null after login")
//                }
//                Toast.makeText(context, "Google Login Successful", Toast.LENGTH_SHORT).show()
//            } else {
//                Toast.makeText(context, "Google Login Failed", Toast.LENGTH_SHORT).show()
//            }
//        })
//    }
//
//
//    private fun handleFailure(e: Exception) {
//        Log.e("CredentialManager", "Error during sign-in", e)
//    }

    private fun navigateToHome() {
        findNavController().navigate(R.id.action_loginFragment_to_homeFragment)
    }

    private fun navigateToRegister() {
        findNavController().navigate(R.id.action_loginFragment_to_registerFragment)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    @RequiresApi(Build.VERSION_CODES.O)
    private fun generateNonce(): String {
        val random = SecureRandom()
        val nonceBytes = ByteArray(16)
        random.nextBytes(nonceBytes)
        return Base64.getUrlEncoder().withoutPadding().encodeToString(nonceBytes)
    }
}
