package com.gagak.farmshields.core.data.remote.client

import com.gagak.farmshields.core.data.remote.response.auth.LoginResponse
import com.gagak.farmshields.core.data.remote.response.auth.RegisterResponse
import com.gagak.farmshields.core.data.remote.response.users.UsersUpdateResponse
import com.gagak.farmshields.core.domain.model.auth.AuthModel
import com.gagak.farmshields.core.domain.model.auth.TokenData
import retrofit2.Call
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.PATCH
import retrofit2.http.POST


interface ApiService {

    // OAuth Google Sign
    @GET("auth/google")
    fun loginWithGoogle(

    ): Call<TokenData>

    // Login
    @POST("auth/login")
    fun login(
        @Body user: AuthModel
    ): Call<LoginResponse>

    @POST("auth/register")
    fun register(
        @Body user: AuthModel
    ): Call <RegisterResponse>

    @PATCH("auth/user")
    suspend fun updateUser(

    ): Response<UsersUpdateResponse>
}