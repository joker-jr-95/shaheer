package com.aait.shaheer.data_layer.model.stories_model

import java.io.Serializable

data class Data(
    val have_story: Boolean,
    val users: List<YourStory>,
    val yourStory: List<MyStory>
):Serializable