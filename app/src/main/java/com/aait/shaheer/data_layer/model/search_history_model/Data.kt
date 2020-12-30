package com.aait.shaheer.data_layer.model.search_history_model

import java.io.Serializable

data class Data(
    val history: List<History>?,
    val max_price: String?,
    val min_price: String?
):Serializable