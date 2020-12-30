package com.aait.shaheer.data_layer.model.filter_model

import com.aait.shaheer.data_layer.model.collection_model.Post
import com.aait.shaheer.data_layer.model.common.BaseResponse
import java.io.Serializable

data class FilterModel(
    val `data`: List<Post>
):BaseResponse(),Serializable