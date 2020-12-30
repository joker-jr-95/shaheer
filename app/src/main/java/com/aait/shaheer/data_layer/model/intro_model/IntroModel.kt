package com.aait.shaheer.data_layer.model.intro_model

import com.aait.shaheer.data_layer.model.common.BaseResponse

data class IntroModel(
    val `data`: List<Data>?
):BaseResponse()