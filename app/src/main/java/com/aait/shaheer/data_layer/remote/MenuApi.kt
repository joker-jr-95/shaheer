package com.aait.shaheer.data_layer.remote

import com.aait.shaheer.data_layer.model.blocked_model.BlockedModel
import com.aait.shaheer.data_layer.model.book_mark_model.BookMarkModel
import com.aait.shaheer.data_layer.model.common.BaseResponse
import com.aait.shaheer.data_layer.model.device_model.DeviceModel
import com.aait.shaheer.data_layer.model.faqs_model.FaqsModel
import com.aait.shaheer.data_layer.model.terms_model.TermsModel
import com.aait.shaheer.data_layer.model.ticket_model.TicketModel
import com.aait.shaheer.data_layer.model.ticket_model.TicketsModel
import com.aait.shaheer.data_layer.remote.Helper
import com.aait.shaheer.domain.repository.base_repository.baseRepository
import io.reactivex.Flowable

import io.reactivex.Observable
import okhttp3.MultipartBody
import retrofit2.Response
import retrofit2.http.*

interface MenuApi {
    @FormUrlEncoded
    @POST("logout")
    fun logout(
        @Field("device_id") device_id:String?= baseRepository.deviceId,
        @Header("Authorization") Authorization:String,
        @Header("lang") lang:String= Helper.language
    ): Observable<Response<BaseResponse>>

    @FormUrlEncoded
    @POST("deviceData")
    fun deviceData(
        @Field("device_id") device_id:String?=baseRepository.deviceId,
        @Header("Authorization") Authorization:String,
        @Header("lang") lang:String= Helper.language
    ): Observable<Response<DeviceModel>>


    @FormUrlEncoded
    @POST("changePassword")
    fun changePassword(
        @Field("current_password") current_password:String,
        @Field("password") password:String,
        @Header("Authorization") Authorization:String,
        @Header("lang") lang:String= Helper.language
    ): Observable<Response<BaseResponse>>

    @FormUrlEncoded
    @POST("blockUser")
    fun blockUser(
        @Field("profile_id") profile_id:String,
        @Header("Authorization") Authorization:String,
        @Header("lang") lang:String= Helper.language
    ): Observable<Response<BaseResponse>>

    @FormUrlEncoded
    @POST("blockList")
    fun blockList(
        @Field("page") page:String,
        @Header("Authorization") Authorization:String,
        @Header("lang") lang:String= Helper.language
    ): Flowable<Response<BlockedModel>>

    @FormUrlEncoded
    @POST("updateDeviceData")
    fun updateDeviceData(
        @Field("device_id") device_id:String?=baseRepository.deviceId,
        @Field("show_ads") show_ads:Boolean?=true,
        @Field("orders_notify") orders_notify:Boolean?=true,
        @Field("near_orders_notify") near_orders_notify:Boolean?=true,
        @Header("Authorization") Authorization:String,
        @Header("lang") lang:String= Helper.language
    ): Observable<Response<BaseResponse>>

    @FormUrlEncoded
    @POST("bookmarkes")
    fun bookMarks(
        @Field("page") page:String,
        @Header("Authorization") Authorization:String,
        @Header("lang") lang:String= Helper.language
    ):Flowable<Response<BookMarkModel>>

    @POST("userTickets")
    fun tickets(
        @Header("Authorization") Authorization:String,
        @Header("lang") lang:String= Helper.language
    ):Observable<Response<TicketsModel>>
    @POST("userTicket")
    fun ticket(
        @Query("ticket_id") ticket_id:String,
        @Header("Authorization") Authorization:String,
        @Header("lang") lang:String= Helper.language
    ):Observable<Response<TicketModel>>

    @POST("customerSatisfaction")
    fun satisfy(
        @Query("ticket_id") ticket_id:String,
        @Query("action") action:String,
        @Header("Authorization") Authorization:String,
        @Header("lang") lang:String= Helper.language
    ):Observable<Response<BaseResponse>>

    @POST("cancelTicket")
    fun cancelTicket(
        @Query("ticket_id") ticket_id:String,
        @Header("Authorization") Authorization:String,
        @Header("lang") lang:String= Helper.language
    ):Observable<Response<BaseResponse>>

    @GET("terms")
    fun terms(
    ):Observable<Response<TermsModel>>

    @GET("privacy")
    fun privacy(
    ):Observable<Response<TermsModel>>

    @GET("faqs")
    fun faqs(
    ): Observable<Response<FaqsModel>>

}