package com.gagak.farmshields.core.data.remote.response.auth

import com.gagak.farmshields.core.domain.model.auth.TokenData

data class LoginResponse(
    val message:String,
    val data: TokenData
)
