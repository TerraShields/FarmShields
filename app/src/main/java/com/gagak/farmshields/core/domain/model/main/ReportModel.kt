package com.gagak.farmshields.core.domain.model.main

import com.google.gson.annotations.SerializedName
import java.io.Serializable

data class ReportModel(
    @SerializedName("user_id")
    val userId: String = "",
    @SerializedName("report_id")
    val reportId: String = "",
    @SerializedName("image")
    val image: String = "",
    @SerializedName("description")
    val description: String = "",
    @SerializedName("sign")
    val sign: String = "",
    @SerializedName("classification_result")
    val classificationResult: String = "",
    val location: GeoPointModel = GeoPointModel("", ""),
    val result: Result = Result(),
    @SerializedName("prediction")
    val prediction: PredictionModel = PredictionModel(0f, 0f, 0f, 0f, 0f, 0f, 0f, 0f, 0f),
    @SerializedName("created_at")
    val createdAt: String = "",
    @SerializedName("delete_countdown")
    val deletedAt: String = ""
): Serializable