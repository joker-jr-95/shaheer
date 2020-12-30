package com.aait.shaheer.data_layer.model.connections_model

import java.io.Serializable

data class Friend(
    val avatar: String?=null,
    val bio: String?=null,
    val category: String?=null,
    val category_id: Int?=null,
    val following_him: Boolean?=null,
    val id: Int?=null,
    val name: String?=null,
    val verified: Boolean?=false,
    var muted: Boolean?=false,
    var blocked: Boolean?=false
):Serializable

