package com.gagak.farmshields.core.domain.model.viewmodel.main

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.cachedIn
import com.gagak.farmshields.core.data.remote.response.main.MainResponse
import com.gagak.farmshields.core.data.remote.response.main.ReportResponse
import com.gagak.farmshields.core.domain.repository.main.MainRepository
import kotlinx.coroutines.launch
import okhttp3.MultipartBody
import okhttp3.RequestBody
import retrofit2.Response

class MainViewModel(
    private val repository: MainRepository,
) : ViewModel() {

    fun getReports() = repository.getReports().cachedIn(viewModelScope)

    fun getReport(page: Int, size: Int, location: Int): LiveData<Response<ReportResponse>> {
        val result = MutableLiveData<Response<ReportResponse>>()
        viewModelScope.launch {
            val response = repository.getReport(page, size, location)
            result.postValue(response)
        }
        return result
    }

    fun report(
        image: MultipartBody.Part,
        latitude: RequestBody,
        longitude: RequestBody,
        sign: RequestBody
    ): LiveData<Response<MainResponse>> {
        val result = MutableLiveData<Response<MainResponse>>()
        viewModelScope.launch {
            val response = repository.report(image, latitude, longitude, sign)
            result.postValue(response)
        }
        return result
    }
}
