package com.gagak.farmshields.core.domain.repository.main

import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.liveData
import com.gagak.farmshields.core.data.adapter.PagingSources
import com.gagak.farmshields.core.data.remote.client.ApiService
import com.gagak.farmshields.core.data.remote.client.ReportApiService
import com.gagak.farmshields.core.data.remote.response.main.MainResponse
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import okhttp3.MultipartBody
import okhttp3.RequestBody
import retrofit2.Response

class MainRepository(
    private val apiService: ApiService,
    private val reportApiService: ReportApiService
) {

    fun getReports() = Pager(
        config = PagingConfig(
            pageSize = 20,
            maxSize = 100,
            enablePlaceholders = false
        ),
        pagingSourceFactory = { PagingSources(apiService) }
    ).liveData

    suspend fun report(
        image: MultipartBody.Part,
        latitude: RequestBody,
        longitude: RequestBody,
        description: RequestBody,
        sign: RequestBody
    ): Response<MainResponse> {
        return withContext(Dispatchers.IO) {
            reportApiService.report(image, latitude, longitude, description, sign)
        }
    }
}
