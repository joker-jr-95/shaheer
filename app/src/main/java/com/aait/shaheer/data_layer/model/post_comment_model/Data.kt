package com.aait.shaheer.data_layer.model.post_comment_model

import java.io.Serializable

data class Comment(
    val comment: String?="",
    val date: String?="now",
    val id: Int?=0,
    val image: String?="",
    val liked: Boolean?=false,
    val num_likes: Int?=0,
    val num_replies: Int?=0,
    val replies: List<Reply>?=null,
    val type: String?="",
    val user: CommentOwner?=null
):Serializable
