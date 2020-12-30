package com.aait.shaheer.data_layer.model.search_history_model

import java.io.Serializable

data class History(
    val bio: String?,
    val following_him: Boolean?,
    val id: Int?,
    val image: String?,
    val item_id: Int?,
    val name: String?,
    val type: String?
):Serializable