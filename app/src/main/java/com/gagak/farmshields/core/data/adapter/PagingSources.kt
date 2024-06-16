package com.gagak.farmshields.core.data.adapter

import androidx.paging.PagingSource
import androidx.paging.PagingState
import com.gagak.farmshields.core.data.remote.client.ApiService
import com.gagak.farmshields.core.domain.model.main.ReportModel

class PagingSources(
    private val apiService: ApiService
) : PagingSource<Int, ReportModel>() {

    override suspend fun load(params: LoadParams<Int>): LoadResult<Int, ReportModel> {
        return try {
            val page = params.key ?: 1
            val response = apiService.getReport(page, params.loadSize)
            val data = response.body()?.data ?: emptyList()

            LoadResult.Page(
                data = data,
                prevKey = if (page == 1) null else page - 1,
                nextKey = if (data.isEmpty()) null else page + 1
            )
        } catch (exception: Exception) {
            LoadResult.Error(exception)
        }
    }

    override fun getRefreshKey(state: PagingState<Int, ReportModel>): Int? {
        return state.anchorPosition?.let { anchorPosition ->
            val anchorPage = state.closestPageToPosition(anchorPosition)
            anchorPage?.prevKey?.plus(1) ?: anchorPage?.nextKey?.minus(1)
        }
    }
}
