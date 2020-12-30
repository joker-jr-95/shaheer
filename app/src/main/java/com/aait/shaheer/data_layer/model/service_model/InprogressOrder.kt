package com.aait.shaheer.data_layer.model.service_model

data class InprogressOrder(
    val call_price: String?,
    val category_id: Int?,
    val category_image: String?,
    val category_name: String?,
    val choose_call: String?,
    val choose_video: String?,
    val client: Client?,
    val coupon_discount: String?,
    val currency: String?,
    val date: String?,
    val deadline: String?,
    val id: Int?,
    val post_id: Int?,
    val price: String?,
    val provider: Provider?,
    val quantity: Int?,
    val status: String?,
    val subtotal_price: String?,
    val total_price: String?,
    val video_price: String?
)