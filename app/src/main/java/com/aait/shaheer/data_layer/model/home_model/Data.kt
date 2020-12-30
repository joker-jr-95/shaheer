package com.aait.shaheer.data_layer.model.home_model

import com.aait.shaheer.data_layer.model.profile_model.Post
import java.io.Serializable

data class Data(
    val current_page: String?,
    val next_page: String?,
    val per_page: String?,
    val posts: List<Post>?,
    val total_pages: Int?
):Serializable