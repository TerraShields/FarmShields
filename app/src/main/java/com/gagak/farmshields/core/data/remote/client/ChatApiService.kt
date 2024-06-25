package com.gagak.farmshields.core.data.remote.client

import com.gagak.farmshields.core.data.remote.response.anita.AnitaResponse
import com.gagak.farmshields.core.data.remote.response.users.UserResponse
import com.gagak.farmshields.core.domain.model.anita.AnitaModel
import com.gagak.farmshields.core.domain.model.viewmodel.anita.AnitaViewModel
import okhttp3.RequestBody
import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.Multipart
import retrofit2.http.POST
import retrofit2.http.Part

interface ChatApiService {
    @POST("api/chat")
    @Multipart
    fun postAnita(
        @Part("caption") caption: AnitaModel
    ): Call<AnitaResponse>
}