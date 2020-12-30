package com.aait.shaheer.data_layer.model.chat_model

import java.io.Serializable

data class Data(
    val messages: List<Message>?,
    val seconduser: Seconduser?
):Serializable