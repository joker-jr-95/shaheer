package com.kareem.datalayer.model.common

import java.io.Serializable

data class UserModel(
    val active: String?=null,
    val avatar: String?=null,
    val balance: String?=null,
    val bio: String?=null,
    val category: String?=null,
    val category_id: Int?=null,
    val code: String?=null,
    val cover: String?=null,
    val currency: String?=null,
    val device_id: String?=null,
    val email: String?=null,
    val following_users: Boolean?=null,
    val gender: String?=null,
    val id: Int?=null,
    val name: String?=null,
    val phone: String?=null,
    val rate: Float?=null,
    val time_zone: String?=null,
    val token: String?=null,
    val verified: Boolean?=null,
    val you_following: Boolean?=null
):Serializable
