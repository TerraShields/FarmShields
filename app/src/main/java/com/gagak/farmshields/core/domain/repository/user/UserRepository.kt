package com.gagak.farmshields.core.domain.repository.user

import com.gagak.farmshields.core.data.local.preferences.AuthPreferences
import com.gagak.farmshields.core.data.remote.client.ApiService
import com.gagak.farmshields.core.data.remote.response.users.UserResponse
import retrofit2.Call

class UserRepository(
    private val apiService: ApiService,
) {
    fun getUser(): Call<UserResponse> {
        return apiService.getUser()
    }
}