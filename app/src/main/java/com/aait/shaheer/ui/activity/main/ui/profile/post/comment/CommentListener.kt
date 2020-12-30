package com.aait.shaheer.ui.activity.main.ui.profile.post.comment

import com.aait.shaheer.data_layer.model.post_comment_model.Comment

interface CommentListener {
    fun onLikeComment(position:Int, item:Comment)
    fun onReplyComment(position:Int, item:Comment)
}