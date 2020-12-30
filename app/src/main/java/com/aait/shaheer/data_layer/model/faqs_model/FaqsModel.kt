package com.aait.shaheer.data_layer.model.faqs_model

import com.aait.shaheer.data_layer.model.common.BaseResponse
import java.io.Serializable

data class FaqsModel(
    val `data`: List<FaqsData>
):BaseResponse(),Serializable