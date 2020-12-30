package com.aait.shaheer.data_layer.model.device_model



import com.aait.shaheer.data_layer.model.common.BaseResponse
import com.google.gson.annotations.SerializedName

data class DeviceModel(
    var `data`: Data
):BaseResponse()