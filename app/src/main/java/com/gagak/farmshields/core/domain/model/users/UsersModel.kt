package com.gagak.farmshields.core.domain.model.users

import com.google.gson.annotations.SerializedName

data class UsersModel(
    @SerializedName("image")
    val image: String,
    @SerializedName("password")
    val password: String,
    @SerializedName("address")
    val address: String,
    @SerializedName("user_id")
    val userId: String,
    @SerializedName("name")
    val name: String,
    @SerializedName("email")
    val email: String
)
