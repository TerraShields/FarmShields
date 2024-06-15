package com.gagak.farmshields.core.domain.model.viewmodel.anita

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.gagak.farmshields.core.data.remote.response.anita.AnitaResponse
import com.gagak.farmshields.core.domain.repository.anita.AnitaRepository
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class AnitaViewModel(private val anitaRepository: AnitaRepository) : ViewModel() {
    private val _chatLiveData = MutableLiveData<String>()
    val chatLiveData: LiveData<String> = _chatLiveData

    fun sendQuestion(question: String) {
        anitaRepository.getAnitaResponse(question)
            .enqueue(object : Callback<AnitaResponse> {
                override fun onResponse(
                    call: Call<AnitaResponse>,
                    response: Response<AnitaResponse>
                ) {
                    if (response.isSuccessful) {
                        val anitaResponse = response.body()?.system ?: "No response"
                        _chatLiveData.value = anitaResponse
                    } else {
                        _chatLiveData.value = "Error: ${response.code()} ${response.message()}"
                    }
                }

                override fun onFailure(call: Call<AnitaResponse>, t: Throwable) {
                    _chatLiveData.value = "Error: ${t.message}"
                }
            })
    }
}
