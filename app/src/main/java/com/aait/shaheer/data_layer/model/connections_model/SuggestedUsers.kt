package com.aait.shaheer.data_layer.model.connections_model

import com.aait.shaheer.data_layer.model.common.BaseResponse
import java.io.Serializable

data class SuggestedUsers(
    val id:Int,
    val name:String,
    val category_id:Int,
    val category:String,
    val bio:String,
    val verified:Boolean,
    val avatar:String,
    val following_him:Boolean
    ): Serializable
