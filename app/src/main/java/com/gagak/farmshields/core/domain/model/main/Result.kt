package com.gagak.farmshields.core.domain.model.main

import com.google.gson.annotations.SerializedName
import java.io.Serializable

data class Result(
    @SerializedName("class")
    val classy: String = "",
    @SerializedName("probability")
    val probability: String = ""
): Serializable
