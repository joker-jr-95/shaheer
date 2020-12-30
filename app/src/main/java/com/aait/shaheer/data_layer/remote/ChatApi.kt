package com.kareem.datalayer.remote

import com.aait.shaheer.data_layer.remote.Helper

import io.reactivex.Observable
import okhttp3.MultipartBody
import retrofit2.Response
import retrofit2.http.*

interface ChatApi {
   /* @POST("getAllRooms")
    fun rooms(
        @Header("user_id") user_id:String,
        @Header("lang") lang:String= Helper.language
    ):Observable<Response<RoomsModel>>

    @FormUrlEncoded
    @POST("deleteRoom")
    fun remove_room(
        @Field("conversationId") conversationId:String,
        @Header("lang") lang:String= Helper.language
    ):Observable<Response<BaseResponse>>

    @FormUrlEncoded
    @POST("getAllMessageRoom2")
    fun chat(
        @Field("reciverId") reciverId:String,
        @Header("user_id") user_id:String,
        @Header("lang") lang:String= Helper.language
    ):Observable<Response<ChatModel>>

    @Multipart
    @POST("uploadFileInMessage")
    fun uploadFileMessage(
        @Part message:MultipartBody.Part,*//*image,pdf*//*
        @Query("type") type:String,
        @Header("lang") lang:String= Helper.language
    ):Observable<Response<UploadChatModel>>
*/
}