package com.aait.shaheer.data_layer.model.helps_model

import com.aait.shaheer.data_layer.model.common.BaseResponse
import java.io.Serializable

data class HelpsModel(
    val `data`: List<Data>?
):BaseResponse(),Serializable