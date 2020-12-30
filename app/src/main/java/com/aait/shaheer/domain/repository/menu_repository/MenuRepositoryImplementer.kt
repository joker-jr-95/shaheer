package com.aait.shaheer.domain.repository.menu_repository

import com.aait.shaheer.data_layer.cache.PreferencesGateway
import com.aait.shaheer.data_layer.cache.preferencesGateway
import com.aait.shaheer.data_layer.model.blocked_model.BlockedModel
import com.aait.shaheer.data_layer.model.book_mark_model.BookMarkModel
import com.aait.shaheer.data_layer.model.common.BaseResponse
import com.aait.shaheer.data_layer.model.device_model.DeviceModel
import com.aait.shaheer.data_layer.model.faqs_model.FaqsModel
import com.aait.shaheer.data_layer.model.terms_model.TermsModel
import com.aait.shaheer.data_layer.model.ticket_model.TicketModel
import com.aait.shaheer.data_layer.model.ticket_model.TicketsModel
import com.aait.shaheer.data_layer.remote.CartApi
import com.aait.shaheer.data_layer.remote.MenuApi
import com.aait.shaheer.domain.repository.auth_repository.prefKeys
import com.aait.shaheer.domain.repository.base_repository.BaseRepositoryImplementer
import com.aait.shaheer.domain.repository.cart_repository.CartRepository
import com.kareem.domain.core.Util
import io.reactivex.Flowable
import io.reactivex.Observable
import io.reactivex.Single
import menuGateWay
import retrofit2.Response

class MenuRepositoryImplementer (private val menuApi: MenuApi = menuGateWay,
                                 private val preferences: PreferencesGateway = preferencesGateway
): MenuRepository, BaseRepositoryImplementer(preferences) {

    /*override fun saveLang(lang: String) {
        preferences.save(prefKeys.LANG,lang).subscribe()
    }

    override fun getLang(): Single<String> {
        return preferences.load(prefKeys.LANG, Util.language)

    }*/

    override fun changePassword(
        current_password: String,
        new_password: String
        ): Observable<Response<BaseResponse>> {
        return menuApi.changePassword(current_password,new_password,loadToken().blockingGet())
    }

    override fun logout(): Observable<Response<BaseResponse>> {
        preferences.clearAll()
        return menuApi.logout(menuRepository.deviceId,loadToken().blockingGet())
    }



    override fun blockList(page: String): Flowable<Response<BlockedModel>> {
        return menuApi.blockList(page,loadToken().blockingGet())
    }

    override fun deviceData(device_id: String?): Observable<Response<DeviceModel>> {
        return menuApi.deviceData(loadUser()?.device_id,loadToken().blockingGet())
    }

    override fun updateNotif(
        device_id: String?,
        showAds: Boolean?,
        isNotify: Boolean?,
        near_orders_notify: Boolean?
    ): Observable<Response<BaseResponse>> {
        return menuApi.updateDeviceData(loadUser()?.device_id,showAds,isNotify,near_orders_notify,loadToken().blockingGet())
    }

    override fun bookMarkes(page: String): Flowable<Response<BookMarkModel>> {
        return menuApi.bookMarks(page,loadToken().blockingGet())
    }

    override fun cancelTicket(ticket_id: String): Observable<Response<BaseResponse>> {
        return menuApi.cancelTicket(ticket_id,loadToken().blockingGet())
    }

    override fun satisfy(ticket_id: String, action: String): Observable<Response<BaseResponse>> {
        return menuApi.satisfy(ticket_id,action,/*"Bearer eyJ0eXAiOiJKV1QiLCJhbGciOiJIUzI1NiJ9.eyJpc3MiOiJodHRwOlwvXC9sb2NhbGhvc3RcL3NoYWhlZXJcL3B1YmxpY1wvYXBpXC9zaWduVXAiLCJpYXQiOjE1ODMwNTkwNDQsIm5iZiI6MTU4MzA1OTA0NCwianRpIjoic0tCblRVUUNGQ3ViRFJLViIsInN1YiI6NCwicHJ2IjoiODdlMGFmMWVmOWZkMTU4MTJmZGVjOTcxNTNhMTRlMGIwNDc1NDZhYSJ9.83DZHby8K2GBsVVw8C36G5Jd6Eyz5yiGSKVGHVL9HHY"*/loadToken().blockingGet())
    }

    override fun faqs(): Observable<Response<FaqsModel>> {
        return menuApi.faqs()
    }

    override fun terms(): Observable<Response<TermsModel>> {
        return menuApi.terms()
    }

    override fun policy(): Observable<Response<TermsModel>> {
        return menuApi.privacy()
    }

    override fun tickets(): Observable<Response<TicketsModel>> {
        return menuApi.tickets(
            /*"Bearer eyJ0eXAiOiJKV1QiLCJhbGciOiJIUzI1NiJ9.eyJpc3MiOiJodHRwOlwvXC9sb2NhbGhvc3RcL3NoYWhlZXJcL3B1YmxpY1wvYXBpXC9zaWduVXAiLCJpYXQiOjE1ODMwNTkwNDQsIm5iZiI6MTU4MzA1OTA0NCwianRpIjoic0tCblRVUUNGQ3ViRFJLViIsInN1YiI6NCwicHJ2IjoiODdlMGFmMWVmOWZkMTU4MTJmZGVjOTcxNTNhMTRlMGIwNDc1NDZhYSJ9.83DZHby8K2GBsVVw8C36G5Jd6Eyz5yiGSKVGHVL9HHY"*/
            loadToken().blockingGet())
    }

    override fun ticket(ticket_id: String): Observable<Response<TicketModel>> {
        return menuApi.ticket(ticket_id,loadToken().blockingGet())
    }


    override fun blockUser(profileId: String): Observable<Response<BaseResponse>> {
        return menuApi.blockUser(profileId,loadToken().blockingGet())
    }

}