package com.aait.shaheer.data_layer.model.stories_model

import java.io.Serializable

data class YourStory(
    val date: String?,
    val image: String?,
    val story_id: Int?,
    val name:String?="",
    val avatar:String?="",
    val seen:Int,
    val user_story:List<UserStory>,
    val user_id: Int?
):Serializable