package com.aait.shaheer.data_layer.model.profile_model.my_following

import java.io.Serializable

data class Profile(
    val active: String?,
    val avatar: String?,
    val balance: String?,
    val bio: String?,
    val category: String?,
    val category_id: Int?,
    val code: String?,
    val cover: String?,
    val currency: String?,
    val device_id: String?,
    val email: String?,
    val following_users: Boolean?,
    val gender: String?,
    val id: Int?,
    val name: String?,
    val phone: String?,
    val rate: String?,
    val time_zone: String?,
    val token: String?,
    val verified: Boolean?,
    val you_following: Boolean?,
    val muted: Boolean?=false,
    val blocked: Boolean?=false
):Serializable