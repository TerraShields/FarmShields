package com.gagak.farmshields.core.data.remote.response.auth

import com.gagak.farmshields.core.domain.model.auth.TokenData

data class OAuthGoogleResponse(
    val data: TokenData
)