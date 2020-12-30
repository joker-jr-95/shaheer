package com.aait.shaheer.data_layer.model.collection_model

import java.io.Serializable

data class User(
    val avatar: String?,
    val bio: String?,
    val category: String?,
    val category_id: Int?,
    val category_image: String?,
    val following_him: Boolean?,
    val id: Int?,
    val name: String?,
    val verified: Boolean?,
    val muted: Boolean?=false,
    val blocked: Boolean?=false
):Serializable