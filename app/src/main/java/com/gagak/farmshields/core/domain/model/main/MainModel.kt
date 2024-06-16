package com.gagak.farmshields.core.domain.model.main

import com.google.gson.annotations.SerializedName

data class MainModel (
    @SerializedName("user_id")
    val userId: String,
    @SerializedName("report_id")
    val reportId: String,
    @SerializedName("latitude")
    val latitude: String,
    @SerializedName("longitude")
    val longitude: String,
    @SerializedName("image")
    val image: String,
    @SerializedName("description")
    val description: String,
    @SerializedName("sign")
    val sign: String,
    @SerializedName("created_at")
    val createdAt: String
)