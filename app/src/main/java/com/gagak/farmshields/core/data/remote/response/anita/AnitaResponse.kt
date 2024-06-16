package com.gagak.farmshields.core.data.remote.response.anita

import com.google.gson.annotations.SerializedName

data class AnitaResponse(
    @SerializedName("system")
    val system: String,
    @SerializedName("caption")
    val caption: String,
    @SerializedName("created_at")
    val createdAt: String
)