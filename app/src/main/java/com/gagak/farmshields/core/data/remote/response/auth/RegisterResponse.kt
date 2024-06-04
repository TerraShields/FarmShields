package com.gagak.farmshields.core.data.remote.response.auth

import com.gagak.farmshields.core.domain.model.auth.AuthModel

data class RegisterResponse(
    val message: String,
    val data: AuthModel

)
