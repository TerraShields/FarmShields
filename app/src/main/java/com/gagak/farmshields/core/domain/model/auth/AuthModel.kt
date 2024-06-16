package com.gagak.farmshields.core.domain.model.auth

import com.google.gson.annotations.SerializedName

data class AuthModel(
    @SerializedName("name")
    val name: String? = null,
    @SerializedName("email")
    val email: String,
    @SerializedName("password")
    val password: String,
    @SerializedName("user_id")
    val userId: String,
    @SerializedName("password_confirmation")
    val passwordConfirmation: String? = null
)