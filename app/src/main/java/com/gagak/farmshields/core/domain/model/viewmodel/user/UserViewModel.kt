package com.gagak.farmshields.core.domain.model.viewmodel.user

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.gagak.farmshields.core.data.remote.response.users.UserResponse
import com.gagak.farmshields.core.data.remote.response.users.UsersUpdateResponse
import com.gagak.farmshields.core.domain.repository.user.UserRepository
import kotlinx.coroutines.launch
import okhttp3.MultipartBody
import okhttp3.RequestBody
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class UserViewModel(private val repository: UserRepository) : ViewModel() {

    private val _userResponse = MutableLiveData<UserResponse>()
    val userResponse: LiveData<UserResponse> get() = _userResponse

    private val _loading = MutableLiveData<Boolean>()
    val loading: LiveData<Boolean> get() = _loading

    private val _error = MutableLiveData<String>()
    val error: LiveData<String> get() = _error

    private val _updateResponse = MutableLiveData<UsersUpdateResponse>()
    val updateResponse: LiveData<UsersUpdateResponse> get() = _updateResponse

    fun getUser() {
        _loading.value = true
        repository.getUser().enqueue(object : Callback<UserResponse> {
            override fun onResponse(call: Call<UserResponse>, response: Response<UserResponse>) {
                _loading.value = false
                if (response.isSuccessful) {
                    _userResponse.value = response.body()
                } else {
                    _error.value = response.errorBody()?.string()
                }
            }

            override fun onFailure(call: Call<UserResponse>, t: Throwable) {
                _loading.value = false
                _error.value = t.message
            }
        })
    }

    fun updateUser(
        image: MultipartBody.Part? = null,
        name: RequestBody? = null,
        address: RequestBody? = null
    ) {
        viewModelScope.launch {
            _loading.value = true
            try {
                val response = repository.updateUser(image, name, address)
                _loading.value = false
                if (response.isSuccessful) {
                    _updateResponse.value = response.body()
                } else {
                    _error.value = response.errorBody()?.string()
                }
            } catch (e: Exception) {
                _loading.value = false
                _error.value = e.message
            }
        }
    }
}


