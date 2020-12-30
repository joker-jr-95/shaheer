package com.aait.shaheer.data_layer.model.helps_model

import java.io.Serializable

data class Data(
    val answer: String?,
    val answer_at: String?,
    val created_at: String?,
    val id: Int?,
    val message: String?,
    val seen: Boolean?,
    val subject: String?,
    val user_id: Int?
):Serializable