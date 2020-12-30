package com.aait.shaheer.data_layer.model.notification_model

import com.aait.shaheer.data_layer.model.common.BaseResponse
import java.io.Serializable

data class NotificationModel(
    val `data`: Data
):BaseResponse(),Serializable