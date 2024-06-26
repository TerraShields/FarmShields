package com.gagak.farmshields.core.data.remote.client

import com.gagak.farmshields.core.data.remote.response.auth.LoginResponse
import com.gagak.farmshields.core.data.remote.response.auth.OAuthGoogleResponse
import com.gagak.farmshields.core.data.remote.response.auth.RegisterResponse
import com.gagak.farmshields.core.data.remote.response.main.MainResponse
import com.gagak.farmshields.core.data.remote.response.main.ReportResponse
import com.gagak.farmshields.core.data.remote.response.users.UserResponse
import com.gagak.farmshields.core.data.remote.response.users.UsersUpdateResponse
import com.gagak.farmshields.core.domain.model.auth.AuthModel
import com.gagak.farmshields.core.domain.model.auth.TokenData
import okhttp3.MultipartBody
import okhttp3.RequestBody
import okhttp3.ResponseBody
import retrofit2.Call
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.Multipart
import retrofit2.http.PATCH
import retrofit2.http.POST
import retrofit2.http.Part
import retrofit2.http.Query


interface ApiService {

    // GetUser
    @GET("auth/user")
    fun getUser(
    ): Call<UserResponse>

    // OAuth Google Sign
    @GET("auth/google")
    fun loginWithGoogle(
        @Query("token") token: String
    ): Call<OAuthGoogleResponse>

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
        @Part image: MultipartBody.Part? = null,
        @Part("name") name: RequestBody? = null,
        @Part("address") address: RequestBody? = null
    ): Response<UsersUpdateResponse>


    // Report Hama

    @GET("report")
    suspend fun getReport(
        @Query("page") page: Int = 1,
        @Query("size") size: Int = 10,
        @Query("location") location: Int = 1
    ): Response<ReportResponse>
}