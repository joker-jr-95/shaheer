package com.aait.shaheer.data_layer.model.stories_model

import java.io.Serializable

data class UserStory (
val story_id: String,
val user_id: String,
val image: String,
val date: String,
val seen: Int
):Serializable
