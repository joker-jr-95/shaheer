package com.aait.shaheer.data_layer.model.profile_model.my_following

import com.aait.shaheer.data_layer.model.connections_model.Friend
import java.io.Serializable

data class Data(
    val all_pages: Int?,
    val current_page: String?,
    val followings: List<Friend>?,
    val followers: List<Friend>?,
    val next_page: String?,
    val per_page: String?,
    val profile: Profile?
):Serializable