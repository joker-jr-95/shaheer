package com.aait.shaheer.data_layer.model.categories_model

import com.aait.shaheer.data_layer.model.common.BaseResponse

data class CategoriesModel(
    val `data`: List<Data>?
):BaseResponse()