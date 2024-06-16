package com.gagak.farmshields.core.data.remote.response.main

import com.gagak.farmshields.core.domain.model.main.PagingModel
import com.gagak.farmshields.core.domain.model.main.ReportModel

data class ReportResponse(
    val data: List<ReportModel>,
    val paging: PagingModel,
)
