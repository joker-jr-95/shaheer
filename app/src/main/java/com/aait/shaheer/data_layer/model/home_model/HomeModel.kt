package com.aait.shaheer.data_layer.model.home_model

import com.aait.shaheer.data_layer.model.common.BaseResponse
import java.io.Serializable

data class HomeModel(
    val `data`: Data?
):BaseResponse(),Serializable