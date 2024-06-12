package com.gagak.farmshields.core.data.remote.response.users

import com.gagak.farmshields.core.domain.model.users.UsersModel
import com.google.gson.annotations.SerializedName

data class UserDetail(
    @SerializedName("user")
    val user: UsersModel
)
