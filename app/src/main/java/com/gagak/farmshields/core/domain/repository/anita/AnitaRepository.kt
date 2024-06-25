package com.gagak.farmshields.core.domain.repository.anita

import com.gagak.farmshields.core.data.remote.client.ChatApiService
import com.gagak.farmshields.core.data.remote.response.anita.AnitaResponse
import com.gagak.farmshields.core.domain.model.anita.AnitaModel
import retrofit2.Call

class AnitaRepository(private val chatApiService: ChatApiService) {
    fun getAnitaResponse(caption: String): Call<AnitaResponse> {
        val anitaModel = AnitaModel(caption)
        return chatApiService.postAnita(anitaModel)
    }
}
