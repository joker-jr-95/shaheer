package com.aait.shaheer.data_layer.model.blocked_model

data class Data(
    val current_page: String?,
    val next_page: String?,
    val per_page: String?,
    val total_pages: Int?,
    val users: List<User>
)