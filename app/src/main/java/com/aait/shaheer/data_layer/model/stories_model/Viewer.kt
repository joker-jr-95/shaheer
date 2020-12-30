package com.aait.shaheer.data_layer.model.stories_model

import java.io.Serializable

data class Viewer(
    val avatar: String?,
    val bio: String?,
    val category_id: Int?,
    val following_him: Boolean?,
    val id: Int?,
    val name: String?,
    val verified: Boolean?
):Serializable