package com.aait.shaheer.data_layer.model.search_history_model

import com.aait.shaheer.data_layer.model.common.BaseResponse
import java.io.Serializable

data class SearchHistoryModel(
    val `data`: Data
):BaseResponse(),Serializable