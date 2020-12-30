package com.aait.shaheer.data_layer.model.chat_model

import com.aait.shaheer.data_layer.model.common.BaseResponse
import java.io.Serializable

data class ChatModel(
    val `data`: Data?
):BaseResponse(),Serializable