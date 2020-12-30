package com.aait.shaheer.data_layer.model.ticket_model

import java.io.Serializable

data class Tickets(
    val answer: String?,
    val avatar: String?,
    val date: String?,
    val id: Int?,
    val images: List<Any>?,
    val make_satisfied: String?,
    val order_id : Int?,
    val status: String?,
    val store_icon: String?,
    val store_name: String?,
    val subject: String?,
    val text: String?,
    val user_id: Int?,
    val username: String?
):Serializable