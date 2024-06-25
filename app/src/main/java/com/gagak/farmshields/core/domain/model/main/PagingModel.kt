package com.gagak.farmshields.core.domain.model.main

import com.google.gson.annotations.SerializedName

data class PagingModel(
    @SerializedName("total_item")
    val total: Int,
    @SerializedName("total_page")
    val totalPages: Int,
    @SerializedName("page")
    val page: Int,
    @SerializedName("size")
    val size: Int
)
