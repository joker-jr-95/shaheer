package com.aait.shaheer.data_layer.model.collection_model

import com.aait.shaheer.data_layer.model.common.BaseResponse

data class CollectionModel(
    val `data`: List<Data>?
):BaseResponse()