package com.gagak.farmshields.core.domain.model.viewmodel.anita

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.gagak.farmshields.core.data.remote.response.anita.AnitaResponse
import com.gagak.farmshields.core.domain.repository.anita.AnitaRepository
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

data class ChatMessage(val message: String, val isUser: Boolean, val createdAt: String = "")

class AnitaViewModel(private val anitaRepository: AnitaRepository) : ViewModel() {
    private val _chatLiveData = MutableLiveData<List<ChatMessage>>()
    val chatLiveData: LiveData<List<ChatMessage>> = _chatLiveData
    private val messages = mutableListOf<ChatMessage>()

    fun sendQuestion(question: String) {
        val userMessage = ChatMessage(question, true)
        messages.add(userMessage)
        _chatLiveData.value = messages.toList()

        anitaRepository.getAnitaResponse(question)
            .enqueue(object : Callback<AnitaResponse> {
                override fun onResponse(
                    call: Call<AnitaResponse>,
                    response: Response<AnitaResponse>
                ) {
                    if (response.isSuccessful) {
                        val anitaResponse = response.body()
                        val systemMessage = ChatMessage(anitaResponse?.system ?: "No response", false, anitaResponse?.createdAt ?: "")
                        messages.add(systemMessage)
                        _chatLiveData.value = messages.toList()
                    } else {
                        val errorMessage = "Error: ${response.code()} ${response.message()}"
                        val systemMessage = ChatMessage(errorMessage, false)
                        messages.add(systemMessage)
                        _chatLiveData.value = messages.toList()
                    }
                }

                override fun onFailure(call: Call<AnitaResponse>, t: Throwable) {
                    val errorMessage = "Error: ${t.message}"
                    val systemMessage = ChatMessage(errorMessage, false)
                    messages.add(systemMessage)
                    _chatLiveData.value = messages.toList()
                }
            })
    }
}