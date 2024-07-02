package com.gagak.farmshields.core.data.remote.client

import com.gagak.farmshields.core.data.remote.response.main.MainResponse
import okhttp3.MultipartBody
import okhttp3.RequestBody
import retrofit2.Response
import retrofit2.http.Multipart
import retrofit2.http.POST
import retrofit2.http.Part

interface ReportApiService {
    // Report Hama
    @Multipart
    @POST("report")
    suspend fun report(
        @Part image: MultipartBody.Part,
        @Part("latitude") latitude: RequestBody,
        @Part("longitude") longitude: RequestBody,
        @Part("sign") sign: RequestBody
    ): Response<MainResponse>
}