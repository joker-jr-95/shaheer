package com.aait.shaheer.data_layer.model.search_model

import com.aait.shaheer.data_layer.model.common.BaseResponse
import java.io.Serializable

data class SearchModel(

    val `data`: Data
):BaseResponse(),Serializable