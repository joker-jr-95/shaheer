package com.aait.shaheer.data_layer.model.service_model

data class Data(
    val closedOrders: List<InprogressOrder>,
    val finishedOrders: List<InprogressOrder>,
    val inprogressOrders: List<InprogressOrder>,
    val openOrders: List<InprogressOrder>
)