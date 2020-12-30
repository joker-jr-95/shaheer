package com.aait.shaheer.data_layer.model.book_mark_model

import com.aait.shaheer.data_layer.model.collection_model.Post

data class Data(
    val all_pages: Int?,
    val current_page: String?,
    val next_page: String?,
    val per_page: String?,
    val posts: List<Post>
)