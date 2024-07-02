package com.gagak.farmshields.core.domain.model.main

import com.google.gson.annotations.SerializedName
import java.io.Serializable

data class PredictionModel (
    @SerializedName("C")
    val c: Float,
    @SerializedName("E")
    val e: Float,
    @SerializedName("N")
    val n: Float,
    @SerializedName("NE")
    val ne: Float,
    @SerializedName("NW")
    val nw: Float,
    @SerializedName("S")
    val s: Float,
    @SerializedName("SE")
    val se: Float,
    @SerializedName("SW")
    val sw: Float,
    @SerializedName("W")
    val w: Float
): Serializable