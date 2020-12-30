package com.aait.shaheer.data_layer.model.session_cred_model

import com.google.gson.annotations.SerializedName
import java.io.Serializable

data class Data(
    val opentok_token: String?,
    val session_token: String?,
    @SerializedName("OPENTOK_API_KEY")
    val api_key: String?

):Serializable