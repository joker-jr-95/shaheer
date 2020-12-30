package com.aait.shaheer.data_layer.remote

import com.aait.shaheer.data_layer.model.OpenChatModel
import com.aait.shaheer.data_layer.model.chat_model.ChatModel
import com.aait.shaheer.data_layer.model.common.BaseResponse
import com.aait.shaheer.data_layer.model.create_search_model.CreateSearchModel
import com.aait.shaheer.data_layer.model.filter_model.FilterModel
import com.aait.shaheer.data_layer.model.help_details_model.HelpDetailsModel
import com.aait.shaheer.data_layer.model.helps_model.HelpsModel
import com.aait.shaheer.data_layer.model.notification_model.NotificationModel
import com.aait.shaheer.data_layer.model.rooms_model.RoomModel
import com.aait.shaheer.data_layer.model.search_history_model.SearchHistoryModel
import com.aait.shaheer.data_layer.model.search_model.SearchModel
import com.aait.shaheer.data_layer.model.session_cred_model.SessionCredModel
import com.aait.shaheer.data_layer.model.upload_model.UploadModel
import io.reactivex.Flowable
import io.reactivex.Observable
import okhttp3.MultipartBody
import retrofit2.Response
import retrofit2.http.*

interface InboxApi {
    @POST("helps")
    fun helps(
        @Header("Authorization") token:String?=null,
        @Header("lang") lang:String= Helper.language
    ): Observable<Response<HelpsModel>>

    @POST("chats")
    fun rooms(
        @Header("Authorization") token:String?=null,
        @Header("lang") lang:String= Helper.language
    ): Observable<Response<RoomModel>>

    @POST("deleteConversation")
    fun deleteChat(
        @Query("conversation_id") conversation_id:String,
        @Header("Authorization") token:String?=null,
        @Header("lang") lang:String= Helper.language
    ): Observable<Response<BaseResponse>>

    @POST("muteConversation")
    fun muteChat(
        @Query("conversation_id") conversation_id:String,
        @Header("Authorization") token:String?=null,
        @Header("lang") lang:String= Helper.language
    ): Observable<Response<BaseResponse>>

    @POST("conversation")
    fun chat(
        @Query("conversation_id") conversation_id:String,
        @Header("Authorization") token:String?=null,
        @Header("lang") lang:String= Helper.language
    ): Observable<Response<ChatModel>>

    @POST("openChat")
    fun openChat(
        @Query("profile_id") profile_id:String,
        @Header("Authorization") token:String?=null,
        @Header("lang") lang:String= Helper.language
    ): Observable<Response<OpenChatModel>>

    @Multipart
    @POST("uploadFile")
    fun upload(
        @Part file:MultipartBody.Part,
        @Header("Authorization") token:String?=null,
        @Header("lang") lang:String= Helper.language
    ):Observable<Response<UploadModel>>


    @POST("getNotifications")
    fun notifications(
        @Query("page") page:String,
        @Header("Authorization") token:String?=null,
        @Header("lang") lang:String= Helper.language
    ): Flowable<Response<NotificationModel>>

    @POST("deleteNotification")
    fun deleteNotification(
        @Query("id") id:String,
        @Header("Authorization") token:String?=null,
        @Header("lang") lang:String= Helper.language
    ): Observable<Response<BaseResponse>>

    @POST("searchHistory")
    fun searchHistory(
        @Header("Authorization") token:String?=null,
        @Header("lang") lang:String= Helper.language
    ): Observable<Response<SearchHistoryModel>>

    @POST("saveSearchHistory")
    fun saveHistory(
        @Query("item_id") item_id:String,
        @Query("type") type:String,
        @Header("Authorization") token:String?=null,
        @Header("lang") lang:String= Helper.language
    ):Observable<Response<BaseResponse>>

    @POST("searchUsers")
    fun searchUsers(
        @Query("name") name:String,
        @Query("page") page:String,
        @Header("Authorization") token:String?=null,
        @Header("lang") lang:String= Helper.language
    ):Flowable<Response<SearchModel>>

    @POST("createSearch")
    fun createSearch(
        @Query("word") name:String,
        @Header("Authorization") token:String?=null,
        @Header("lang") lang:String= Helper.language
    ):Observable<Response<CreateSearchModel>>

    @FormUrlEncoded
    @POST("searchCategoryFilter")
    fun filter(
        @Field("min_price") min_price:String?=null,
        @Field("max_price") max_price:String?=null,
        @Field("rate") rate:String?=null,
        @Field("category_ids[]") category_ids:MutableList<Int>?=null,
        @Header("Authorization") token:String?=null,
        @Header("lang") lang:String= Helper.language
    ):Observable<Response<FilterModel>>

    @POST("helpDetails")
    fun helpDetails(
        @Query("id") id:String,
        @Header("Authorization") token:String?=null,
        @Header("lang") lang:String= Helper.language
    ):Observable<Response<HelpDetailsModel>>

    @POST("videoChat")
    fun sessionCreds(
        @Query("conversation_id") conversation_id:String,
        @Header("Authorization") token:String?=null,
        @Header("lang") lang:String= Helper.language
    ):Observable<Response<SessionCredModel>>
}
