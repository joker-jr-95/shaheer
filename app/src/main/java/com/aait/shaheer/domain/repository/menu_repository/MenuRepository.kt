package com.aait.shaheer.domain.repository.menu_repository

import com.aait.shaheer.data_layer.model.blocked_model.BlockedModel
import com.aait.shaheer.data_layer.model.book_mark_model.BookMarkModel
import com.aait.shaheer.data_layer.model.common.BaseResponse
import com.aait.shaheer.data_layer.model.device_model.DeviceModel
import com.aait.shaheer.data_layer.model.faqs_model.FaqsModel
import com.aait.shaheer.data_layer.model.terms_model.TermsModel
import com.aait.shaheer.data_layer.model.ticket_model.TicketModel
import com.aait.shaheer.data_layer.model.ticket_model.TicketsModel
import io.reactivex.Flowable
import io.reactivex.Observable
import io.reactivex.Single
import retrofit2.Response


val menuRepository by lazy { MenuRepositoryImplementer() }

interface MenuRepository {
    /*fun saveLang(lang:String)
    fun getLang(): Single<String>*/
    fun changePassword(
        current_password: String,
        new_password: String
        ): Observable<Response<BaseResponse>>
    fun logout():Observable<Response<BaseResponse>>
    fun blockList(page:String):Flowable<Response<BlockedModel>>
    fun blockUser(profileId:String):Observable<Response<BaseResponse>>
    fun deviceData(device_id:String?=null):Observable<Response<DeviceModel>>
    fun  updateNotif(device_id:String?=null,showAds:Boolean?=null,isNotify: Boolean?=null,near_orders_notify:Boolean?=null): Observable<Response<BaseResponse>>
    fun bookMarkes(page: String):Flowable<Response<BookMarkModel>>
    fun cancelTicket(ticket_id:String):Observable<Response<BaseResponse>>
    fun satisfy(ticket_id:String,action:String):Observable<Response<BaseResponse>>
    fun faqs():Observable<Response<FaqsModel>>
    fun terms():Observable<Response<TermsModel>>
    fun policy():Observable<Response<TermsModel>>
    fun tickets():Observable<Response<TicketsModel>>
    fun ticket(ticket_id: String):Observable<Response<TicketModel>>
}
