package com.aait.shaheer.data_layer.model.post_comment_model

import java.io.Serializable

data class Reply(
    val comment: String?,
    val date: String?,
    val id: Int?,
    val image: String?,
    val liked: Boolean?,
    val num_likes: Int?,
    val type: String?,
    val user: User?
):Serializable