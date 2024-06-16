package com.gagak.farmshields.core.domain.model.main

import com.google.gson.annotations.SerializedName

data class Result(
    @SerializedName("class")
    val classy:String,
    @SerializedName("probability")
    val probability:String
)
