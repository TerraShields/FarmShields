package com.gagak.farmshields.core.data.remote.client

import com.gagak.farmshields.core.data.remote.response.auth.LoginResponse
import com.gagak.farmshields.core.data.remote.response.auth.RegisterResponse
import com.gagak.farmshields.core.data.remote.response.users.UserResponse
import com.gagak.farmshields.core.data.remote.response.users.UsersUpdateResponse
import com.gagak.farmshields.core.domain.model.auth.AuthModel
import com.gagak.farmshields.core.domain.model.auth.TokenData
import okhttp3.MultipartBody
import okhttp3.RequestBody
import retrofit2.Call
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.Multipart
import retrofit2.http.PATCH
import retrofit2.http.POST
import retrofit2.http.Part


interface ApiService {

    // GetUser
    @GET("auth/user")
    fun getUser(
    ): Call<UserResponse>

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
    @Multipart
    suspend fun updateUser(
        @Part image: MultipartBody.Part,
        @Part("name") name: RequestBody,
        @Part("address") address: RequestBody
    ): Response<UsersUpdateResponse>
}