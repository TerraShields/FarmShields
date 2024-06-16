package com.gagak.farmshields.core.data.remote.response.main

import com.gagak.farmshields.core.domain.model.main.MainModel

data class MainResponse (
    val message: String,
    val data: MainModel
)