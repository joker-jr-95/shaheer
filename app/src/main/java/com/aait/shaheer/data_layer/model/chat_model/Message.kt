package com.aait.shaheer.data_layer.model.chat_model

import java.io.Serializable

data class Message(
    val avatar: String?="",
    val content: String?="",
    val date: String?="",
    val seen: String?="",
    val sender: String?="",
    val type: String?="",
    val user_id: Int?=0,
    val username: String?=""
):Serializable