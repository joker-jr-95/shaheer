package com.aait.shaheer.data_layer.model.profile_model

import java.io.Serializable

data class Data(
    val all_pages: Int?,
    val current_page: String?,
    val next_page: String?,
    val per_page: String?,
    val posts: List<Post>?,
    val profile: Profile?
):Serializable