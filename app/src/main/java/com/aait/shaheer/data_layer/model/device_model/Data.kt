package com.aait.shaheer.data_layer.model.device_model



import com.google.gson.annotations.SerializedName

data class Data(
    @SerializedName("device_id")
    var deviceId: String?,
    var lang: String?,
    @SerializedName("orders_notify")
    var ordersNotify: String?,
    @SerializedName("near_orders_notify")
    var near_orders_notify: String?,
    @SerializedName("show_ads")
    var showAds: String?

)