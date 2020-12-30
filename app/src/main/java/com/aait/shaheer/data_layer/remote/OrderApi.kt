package com.aait.shaheer.data_layer.remote

import com.aait.shaheer.data_layer.model.collection_model.CollectionModel
import com.aait.shaheer.data_layer.model.common.BaseResponse
import com.aait.shaheer.data_layer.model.post_comment_model.PostCommentModel
import com.aait.shaheer.data_layer.model.service_model.ServiceModel
import io.reactivex.Observable
import retrofit2.Response
import retrofit2.http.Header
import retrofit2.http.POST
import retrofit2.http.Query

interface OrderApi {
    @POST("acceptOrder")
    fun acceptOrder(
        @Query("order_id") order_id:String,
        @Header("Authorization") token:String?=null,
        @Header("lang") lang:String= Helper.language
    ): Observable<Response<BaseResponse>>

    @POST("refuseOrder")
    fun refuseOrder(
        @Query("order_id") order_id:String,
        @Header("Authorization") token:String?=null,
        @Header("lang") lang:String= Helper.language
    ): Observable<Response<BaseResponse>>

    @POST("withdrawOrder")
    fun closeSoldOrder(
        @Query("order_id") order_id:String,
        @Query("close_reason") close_reason:String,
        @Header("Authorization") token:String?=null,
        @Header("lang") lang:String= Helper.language
    ): Observable<Response<BaseResponse>>

    @POST("closeOrder")
    fun closeBoughtOrder(
        @Query("order_id") order_id:String,
        @Query("close_reason") close_reason:String,
        @Header("Authorization") token:String?=null,
        @Header("lang") lang:String= Helper.language
    ): Observable<Response<BaseResponse>>

    @POST("finishOrder")
    fun finishOrder(
        @Query("order_id") order_id:String,
        @Header("Authorization") token:String?=null,
        @Header("lang") lang:String= Helper.language
    ): Observable<Response<BaseResponse>>

    @POST("createTicket")
    fun reportOrder(
        @Query("order_id") order_id:String,
        @Query("subject") subject:String,
        @Query("text") text:String,
        @Header("Authorization") token:String?=null,
        @Header("lang") lang:String= Helper.language
    ): Observable<Response<BaseResponse>>

    @POST("deleteOrder")
    fun deleteOrder(
        @Query("order_id") order_id:String,
        @Header("Authorization") token:String?=null,
        @Header("lang") lang:String= Helper.language
    ): Observable<Response<BaseResponse>>

    @POST("ratePostAndUser")
    fun rate(
        @Query("post_id") post_id:String,
        @Query("user_id") user_id:String,
        @Query("rate") rate:String,
        @Query("comment") comment:String,
        @Header("Authorization") token:String?=null,
        @Header("lang") lang:String= Helper.language
    ): Observable<Response<BaseResponse>>

    @POST("soldOrders")
    fun soldOrders(
        @Header("Authorization") token:String?=null,
        @Header("lang") lang:String= Helper.language
    ):Observable<Response<ServiceModel>>
    @POST("boughtOrders")
    fun boughtOrders(
        @Header("Authorization") token:String?=null,
        @Header("lang") lang:String= Helper.language
    ):Observable<Response<ServiceModel>>

    @POST("collections")
    fun collections(
        @Header("Authorization") token:String?=null,
        @Header("lang") lang:String= Helper.language
    ):Observable<Response<CollectionModel>>
}