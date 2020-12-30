package com.aait.shaheer.data_layer.model.stories_model

import java.io.Serializable

data class MyStory(
val story_id: String,
val user_id: String,
val image: String,
val date: String,
val seen: Int,
val viewers: List<Viewer>

):Serializable
