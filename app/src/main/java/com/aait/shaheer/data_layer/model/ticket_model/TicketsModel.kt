package com.aait.shaheer.data_layer.model.ticket_model

import com.aait.shaheer.data_layer.model.common.BaseResponse
import java.io.Serializable

data class TicketsModel(
    val `data`: List<Tickets>
):BaseResponse(),Serializable