package com.gagak.farmshields.core.domain.repository.auth

import com.gagak.farmshields.core.data.remote.client.ApiService
import com.gagak.farmshields.core.data.remote.response.auth.LoginResponse
import com.gagak.farmshields.core.data.remote.response.auth.OAuthGoogleResponse
import com.gagak.farmshields.core.data.remote.response.auth.RegisterResponse
import com.gagak.farmshields.core.domain.model.auth.AuthModel
import com.gagak.farmshields.core.domain.model.auth.TokenData
import retrofit2.Call

class AuthRepository(
    private val apiService: ApiService
) {

    fun login(authModel: AuthModel): Call<LoginResponse>{
        return apiService.login(authModel)
    }

    fun register(authModel: AuthModel): Call<RegisterResponse>{
        return apiService.register(authModel)
    }

//    fun loginWithGoogle(token: String): Call<OAuthGoogleResponse> {
//        return apiService.loginWithGoogle(token)
//    }

//    fun exchangeCodeForToken(code: String): Call<OAuthGoogleResponse> {
//        return apiService.exchangeCodeForToken(code)
//    }
}
