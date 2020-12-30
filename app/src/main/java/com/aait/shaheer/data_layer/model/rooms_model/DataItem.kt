package com.aait.shaheer.data_layer.model.rooms_model

import java.io.Serializable

data class DataItem(val date: String = "",
                    val sender: String = "",
                    val userId: Int = 0,
                    val id: Int = 0,
                    val avatar: String = "",
                    val type: String = "",
                    val seconduser: Seconduser,
                    val content: String = "",
                    val seen: String = "",
                    val username: String = ""):Serializable