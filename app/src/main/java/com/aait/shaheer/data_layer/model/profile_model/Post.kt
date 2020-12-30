package com.aait.shaheer.data_layer.model.profile_model

import java.io.Serializable

data class Post(
    val bookmarked: Boolean?,
    val call_price: String?,
    val category_id: Int?,
    val content: String?,
    val currency: String?,
    val date: String?,
    val deadline: String?,
    val files: List<File>?,
    val id: Int?,
    val in_cart: Boolean?,
    val liked: Boolean?,
    val num_comments: Int?,
    val num_likes: Int?,
    val num_repost: Int?,
    val num_selles: Int?,
    val parent_id: Int?,
    val pin: Boolean?,
    val price: String?,
    val rating: String?,
    val repost_msg: String?,
    val reposted: Boolean?,
    val type: String?,
    val user: User?,
    val video_price: String?,
    val blocked: Boolean?=false,
    val muted: Boolean?=false


):Serializable