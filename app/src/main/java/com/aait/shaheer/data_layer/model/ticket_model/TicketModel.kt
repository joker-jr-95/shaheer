package com.aait.shaheer.data_layer.model.ticket_model


import com.aait.shaheer.data_layer.model.common.BaseResponse
import java.io.Serializable

data class TicketModel(
    val `data`: Tickets
):BaseResponse(),Serializable