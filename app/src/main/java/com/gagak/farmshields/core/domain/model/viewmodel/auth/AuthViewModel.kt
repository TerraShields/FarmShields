package com.gagak.farmshields.core.domain.model.viewmodel.auth

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.gagak.farmshields.core.data.local.preferences.AuthPreferences
import com.gagak.farmshields.core.data.remote.response.auth.LoginResponse
import com.gagak.farmshields.core.data.remote.response.auth.OAuthGoogleResponse
import com.gagak.farmshields.core.data.remote.response.auth.RegisterResponse
import com.gagak.farmshields.core.domain.model.auth.AuthModel
import com.gagak.farmshields.core.domain.model.auth.TokenData
import com.gagak.farmshields.core.domain.repository.auth.AuthRepository
import okhttp3.ResponseBody
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class AuthViewModel(
    private val repository: AuthRepository
) : ViewModel() {

    // login response
    private val _loginResponse = MutableLiveData<Boolean>()
    val loginResponse: LiveData<Boolean> get() = _loginResponse

    // register response
    private val _registerResponse = MutableLiveData<RegisterResponse>()
    val registerResponse: LiveData<RegisterResponse> get() = _registerResponse

    private val _authToken = MutableLiveData<String>()
    val authToken: LiveData<String> = _authToken

    // loading
    private val _loading = MutableLiveData<Boolean>()
    val loading: LiveData<Boolean> get() = _loading

    // error
    private val _error = MutableLiveData<String>()
    val error: LiveData<String> get() = _error

    // get endpoint login
    fun login(email: String, password: String) {
        _loading.value = true
        val authModel = AuthModel(name = "", email = email, password = password, userId = "")
        repository.login(authModel).enqueue(object : Callback<LoginResponse> {
            override fun onResponse(call: Call<LoginResponse>, response: Response<LoginResponse>) {
                _loading.value = false
                if (response.isSuccessful) {
                    _authToken.value = response.body()?.data?.token
                    _loginResponse.value = true
                } else {
                    _loginResponse.value = false
                    _error.value = response.errorBody()?.string()
                }
            }

            override fun onFailure(call: Call<LoginResponse>, t: Throwable) {
                _loading.value = false
                _loginResponse.value = false
                _error.value = t.message
            }
        })
    }

    // get endpoint register
    fun register(authModel: AuthModel) {
        _loading.value = true
        repository.register(authModel).enqueue(object : Callback<RegisterResponse> {
            override fun onResponse(call: Call<RegisterResponse>, response: Response<RegisterResponse>) {
                _loading.value = false
                if (response.isSuccessful) {
                    _registerResponse.value = response.body()
                } else {
                    _error.value = response.errorBody()?.string()
                }
            }

            override fun onFailure(call: Call<RegisterResponse>, t: Throwable) {
                _loading.value = false
                _error.value = t.message
            }
        })
    }

    // OAuth Google
//    fun loginWithGoogle(token: String): LiveData<Boolean> {
//        val loginResponse = MutableLiveData<Boolean>()
//        _loading.value = true
//        repository.loginWithGoogle(token).enqueue(object : Callback<OAuthGoogleResponse> {
//            override fun onResponse(call: Call<OAuthGoogleResponse>, response: Response<OAuthGoogleResponse>) {
//                _loading.value = false
//                if (response.isSuccessful) {
//                    try {
//                        val responseBody = response.body()
//                        Log.d("AuthViewModel", "Response Body: ${responseBody.toString()}")
//                        if (responseBody != null && responseBody.data.token.isNotEmpty()) {
//                            _authToken.value = responseBody.data.token
//                            loginResponse.value = true
//                        } else {
//                            Log.e("AuthViewModel", "Token is null or empty in response")
//                            _error.value = "Token is null or empty"
//                            loginResponse.value = false
//                        }
//                    } catch (e: Exception) {
//                        Log.e("AuthViewModel", "Error parsing response: ${e.message}")
//                        loginResponse.value = false
//                    }
//                } else {
//                    Log.e("AuthViewModel", "Error: ${response.errorBody()?.string()}")
//                    _error.value = response.errorBody()?.string()
//                    loginResponse.value = false
//                }
//            }
//
//            override fun onFailure(call: Call<OAuthGoogleResponse>, t: Throwable) {
//                _loading.value = false
//                loginResponse.value = false
//                Log.e("AuthViewModel", "Failure: ${t.message}")
//                _error.value = t.message
//            }
//        })
//        return loginResponse
//    }

    // Redirect
//    fun exchangeCodeForToken(code: String): LiveData<Boolean> {
//        val result = MutableLiveData<Boolean>()
//        repository.exchangeCodeForToken(code).enqueue(object : Callback<OAuthGoogleResponse> {
//            override fun onResponse(call: Call<OAuthGoogleResponse>, response: Response<OAuthGoogleResponse>) {
//                if (response.isSuccessful) {
//                    _authToken.value = response.body()?.data?.token
//                    result.value = true
//                } else {
//                    result.value = false
//                }
//            }
//
//            override fun onFailure(call: Call<OAuthGoogleResponse>, t: Throwable) {
//                result.value = false
//            }
//        })
//        return result
//    }
}
