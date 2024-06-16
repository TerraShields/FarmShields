package com.gagak.farmshields.core.domain.model.main

import com.google.gson.annotations.SerializedName

data class GeoPointModel(
    @SerializedName("_latitude")
    val latitude: String,
    @SerializedName("_longitude")
    val longitude: String,
)
