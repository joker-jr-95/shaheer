package com.aait.shaheer.ui.activity.main.ui.profile.post.comment

import com.aait.shaheer.data_layer.model.post_comment_model.Comment
import com.aait.shaheer.data_layer.model.post_comment_model.Reply

interface ReplyListener {
    fun onLikeReply(position:Int, item: Reply)
    fun onReplyComment(position:Int, item: Reply)
}