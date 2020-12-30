package com.aait.shaheer.ui.activity.connect

import com.aait.shaheer.data_layer.model.connections_model.Friend

interface OnFollowListener {
    fun onFollow(
        item: Friend,
        isFav: Boolean
    )
}