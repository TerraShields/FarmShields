package com.gagak.farmshields.core.domain.repository.user

import com.gagak.farmshields.core.data.remote.client.ApiService
import com.gagak.farmshields.core.data.remote.response.users.UserResponse
import com.gagak.farmshields.core.data.remote.response.users.UsersUpdateResponse
import okhttp3.MultipartBody
import okhttp3.RequestBody
import retrofit2.Call
import retrofit2.Response

class UserRepository(private val apiService: ApiService) {
    fun getUser(): Call<UserResponse> {
        return apiService.getUser()
    }

    suspend fun updateUser(
        image: MultipartBody.Part? = null,
        name: RequestBody? = null,
        address: RequestBody? = null
    ): Response<UsersUpdateResponse> {
        return apiService.updateUser(image, name, address)
    }
}

