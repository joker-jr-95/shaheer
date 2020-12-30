package com.aait.shaheer.data_layer.model.common

import java.io.Serializable

open class BaseResponse(
    val key: String?="",
    val msg: String?="",
    val code: Int?=200,
    val value: String?="1"
):Serializable
