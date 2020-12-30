package com.aait.shaheer.data_layer.model.notification_model

import java.io.Serializable

data class Data(
    val all_pages: Int?,
    val current_page: String?,
    val next_page: String?,
    val notifications: List<Notification>,
    val per_page: String?
):Serializable