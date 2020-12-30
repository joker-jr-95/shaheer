package com.aait.shaheer.ui.activity.main.ui.home

import com.aait.shaheer.data_layer.model.profile_model.Post

interface ShareListenr {
    fun onCopy(post: Post)
    fun onShare(post: Post)
    fun onBookMark(post: Post)
    fun onPersonClick(profileId:String,post: Post)

}