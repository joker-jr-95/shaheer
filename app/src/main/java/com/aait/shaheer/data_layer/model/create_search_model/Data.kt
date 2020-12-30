package com.aait.shaheer.data_layer.model.create_search_model

import com.aait.shaheer.data_layer.model.search_history_model.History
import java.io.Serializable

data class Data(
    val categories: List<History>?,
    val users: List<History>?
):Serializable