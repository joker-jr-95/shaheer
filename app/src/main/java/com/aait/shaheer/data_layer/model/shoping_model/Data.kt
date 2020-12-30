package com.aait.shaheer.data_layer.model.shoping_model

data class Data(
    val avatar: String?,
    val call_price: String?,
    val category_id: Int?,
    val category_image: String?,
    val category_name: String?,
    val choose_call: String?,
    val choose_video: String?,
    val client_name: String?,
    val id: Int?,
    val post_id: Int?,
    val price: String?,
    val quantity: Int?,
    val total_price: String?,
    val video_price: String?,
    var isItemSelect:Boolean?=false
)