package com.aait.shaheer.data_layer.model.stories_model

import com.aait.shaheer.data_layer.model.common.BaseResponse
import java.io.Serializable

data class StoriesModel(
    val `data`: Data?
):BaseResponse(),Serializable