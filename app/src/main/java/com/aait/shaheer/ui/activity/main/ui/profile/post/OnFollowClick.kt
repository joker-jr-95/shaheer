package com.aait.shaheer.ui.activity.main.ui.profile.post

import com.aait.shaheer.data_layer.model.connections_model.Friend

interface OnMenuClicked {
    fun onMenuClicked(item: Friend)
    fun onFollowClicked()
    fun onMuteClicked()
    fun onBlockClicked()

}