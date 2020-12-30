package com.aait.shaheer.data_layer.model

import com.google.gson.annotations.SerializedName
import java.io.Serializable

class ItemCartItem(
    @SerializedName("choose_call")
    var choose_call: String?,
    @SerializedName("choose_video")
    var choose_video: String?,
    @SerializedName("id")
    var id: String?,
    @SerializedName("quantity")
    var quantity: String?
):Serializable