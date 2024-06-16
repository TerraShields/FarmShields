package com.gagak.farmshields.core.domain.repository.main

import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.liveData
import com.gagak.farmshields.core.data.adapter.PagingSources
import com.gagak.farmshields.core.data.remote.client.ApiService

class MainRepository(private val apiService: ApiService) {

    fun getReports() = Pager(
        config = PagingConfig(
            pageSize = 20,
            maxSize = 100,
            enablePlaceholders = false
        ),
        pagingSourceFactory = { PagingSources(apiService) }
    ).liveData
}
