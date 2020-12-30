package com.aait.shaheer.data_layer.model.post_comment_model

import com.aait.shaheer.data_layer.model.common.BaseResponse
import java.io.Serializable

data class PostCommentModel(
    val `data`: List<Comment>?
):BaseResponse(),Serializable