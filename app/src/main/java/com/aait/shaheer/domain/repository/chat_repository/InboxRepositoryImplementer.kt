package com.aait.shaheer.domain.repository.chat_repository

import cartGateway
import com.aait.shaheer.data_layer.cache.PreferencesGateway
import com.aait.shaheer.data_layer.cache.preferencesGateway
import com.aait.shaheer.data_layer.model.ItemCartItem
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
import com.aait.shaheer.data_layer.model.shoping_model.ShopingModel
import com.aait.shaheer.data_layer.model.upload_model.UploadModel
import com.aait.shaheer.data_layer.remote.CartApi
import com.aait.shaheer.data_layer.remote.InboxApi
import com.aait.shaheer.domain.repository.base_repository.BaseRepositoryImplementer
import inboxGateWay
import io.reactivex.Flowable
import io.reactivex.Observable
import okhttp3.MultipartBody
import retrofit2.Response

class InboxRepositoryImplementer(private val inboxApi: InboxApi = inboxGateWay,
                                 preferences: PreferencesGateway = preferencesGateway
):InboxRepository,BaseRepositoryImplementer(preferences) {
    override fun getHelps(): Observable<Response<HelpsModel>> {
        return inboxApi.helps(loadToken().blockingGet())
    }

    override fun rooms(): Observable<Response<RoomModel>> {
        return inboxApi.rooms(loadToken().blockingGet())
    }

    override fun openChat(profile_id: String): Observable<Response<OpenChatModel>> {
        return inboxApi.openChat(profile_id,loadToken().blockingGet())
    }

    override fun uploadFile(uploadFile: MultipartBody.Part): Observable<Response<UploadModel>> {
        return inboxApi.upload(uploadFile,loadToken().blockingGet())
    }

    override fun muteChat(conversation_id: String): Observable<Response<BaseResponse>> {
        return inboxApi.muteChat(conversation_id,loadToken().blockingGet())
    }

    override fun deleteChat(conversation_id: String): Observable<Response<BaseResponse>> {
        return inboxApi.deleteChat(conversation_id,loadToken().blockingGet())
    }

    override fun chat(conversation_id: String): Observable<Response<ChatModel>> {
        return inboxApi.chat(conversation_id,loadToken().blockingGet())
    }

    override fun getNotifications(page: String): Flowable<Response<NotificationModel>> {
        return inboxApi.notifications(page,loadToken().blockingGet())
    }

    override fun deleteNotification(id: String): Observable<Response<BaseResponse>> {
        return inboxApi.deleteNotification(id,loadToken().blockingGet())
    }

    override fun searchHistory(): Observable<Response<SearchHistoryModel>> {
        return inboxApi.searchHistory(loadToken().blockingGet())
    }

    override fun saveHistory(item_id: String, type: String): Observable<Response<BaseResponse>> {
        return inboxApi.saveHistory(item_id, type,loadToken().blockingGet())
    }

    override fun searchUsers(name: String, page: String): Flowable<Response<SearchModel>> {
        return inboxApi.searchUsers(name, page,loadToken().blockingGet())
    }

    override fun createSearch(word: String): Observable<Response<CreateSearchModel>> {
        return inboxApi.createSearch(word,loadToken().blockingGet())
    }

    override fun filter(
        min_price: String?,
        max_price: String?,
        rate: String?,
        category_ids: MutableList<Int>?
    ): Observable<Response<FilterModel>> {
        return inboxApi.filter(min_price, max_price, rate, category_ids,loadToken().blockingGet())
    }

    override fun helpDetails(help_id: String): Observable<Response<HelpDetailsModel>> {
        return inboxApi.helpDetails(help_id,loadToken().blockingGet())
    }

    override fun getCreds(conversation_id: String): Observable<Response<SessionCredModel>> {
        return inboxApi.sessionCreds(conversation_id,loadToken().blockingGet() )
    }
}
