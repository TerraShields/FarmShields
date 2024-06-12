package com.gagak.farmshields.core.data.remote.response.users

import com.google.gson.annotations.SerializedName

data class UsersUpdateResponse(
    @SerializedName("message")
    val message: String?,
    @SerializedName("errors")
    val errors: String?
)
