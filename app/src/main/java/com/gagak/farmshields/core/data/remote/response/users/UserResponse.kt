package com.gagak.farmshields.core.data.remote.response.users

import com.gagak.farmshields.core.domain.model.users.UsersModel
import com.google.gson.annotations.SerializedName

data class UserResponse(
    @SerializedName("data")
    val data: UserDetail
)
