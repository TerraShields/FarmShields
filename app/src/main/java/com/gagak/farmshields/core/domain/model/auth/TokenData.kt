package com.gagak.farmshields.core.domain.model.auth

import com.google.gson.annotations.SerializedName

data class TokenData(
    @SerializedName("token")
    val token: String
)
