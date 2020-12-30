package com.aait.shaheer.ui.activity.main.ui.cart

import com.aait.shaheer.data_layer.model.shoping_model.Data

interface CartListener {
    fun onCallListener(
        item: Data,
        position: Int,
        checked: Boolean,
        quantity: Float
    )
    fun onVideo(
        item: Data,
        position: Int,
        checked: Boolean,
        quantity: Float
    )
    fun onSelectItem(
        item: Data,
        position: Int,
        select: Boolean,
        extra: Float,
        quantity: Float
    )
    fun onPlus(
        item: Data,
        position: Int,
        extra: Float
    )
    fun onMinus(
        item: Data,
        position: Int,
        extra: Float
    )
}