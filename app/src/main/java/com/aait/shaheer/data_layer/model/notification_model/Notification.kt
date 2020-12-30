package com.aait.shaheer.data_layer.model.notification_model

import java.io.Serializable

data class Notification(
    val `data`: DataX?,
    val date: String?,
    val id: Int?,
    val image: String?,
    val key: String?,
    val name: String?,
    val seen: String?,
    val text: String?,
    val user_id: Int?
):Serializable