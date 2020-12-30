package com.aait.shaheer.domain.repository.chat_repository

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
import com.aait.shaheer.data_layer.remote.Helper
import io.reactivex.Flowable
import io.reactivex.Observable
import okhttp3.MultipartBody
import org.intellij.lang.annotations.Flow
import retrofit2.Response
import retrofit2.http.*


val inboxRepository by lazy { InboxRepositoryImplementer() }

interface InboxRepository{
    fun getHelps():Observable<Response<HelpsModel>>
    fun rooms():Observable<Response<RoomModel>>
    fun openChat(profile_id:String):Observable<Response<OpenChatModel>>
    fun uploadFile(uploadFile:MultipartBody.Part):Observable<Response<UploadModel>>
    fun muteChat(conversation_id:String): Observable<Response<BaseResponse>>
    fun deleteChat(conversation_id:String): Observable<Response<BaseResponse>>
    fun chat(conversation_id: String):Observable<Response<ChatModel>>
    fun getNotifications(page:String):Flowable<Response<NotificationModel>>
    fun deleteNotification(id:String):Observable<Response<BaseResponse>>
    fun searchHistory(): Observable<Response<SearchHistoryModel>>
    fun saveHistory(
        item_id:String,
        type:String
    ): Observable<Response<BaseResponse>>
    fun searchUsers(
         name:String,
         page:String
    ): Flowable<Response<SearchModel>>
    fun createSearch(
         word:String
    ): Observable<Response<CreateSearchModel>>
    fun filter(
        min_price:String?=null,
        max_price:String?=null,
        rate:String?=null,
        category_ids:MutableList<Int>?
    ):Observable<Response<FilterModel>>

    fun helpDetails(
        help_id:String
    ):Observable<Response<HelpDetailsModel>>
    fun getCreds(
        conversation_id:String
    ):Observable<Response<SessionCredModel>>
}
