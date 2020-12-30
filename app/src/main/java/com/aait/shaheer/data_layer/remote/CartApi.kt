package com.aait.shaheer.data_layer.remote

import com.aait.shaheer.data_layer.model.ItemCartItem
import com.aait.shaheer.data_layer.model.common.BaseResponse
import com.aait.shaheer.data_layer.model.register_model.RegisterModel
import com.aait.shaheer.data_layer.model.shoping_model.ShopingModel
import io.reactivex.Observable
import retrofit2.Response
import retrofit2.http.Field
import retrofit2.http.FormUrlEncoded
import retrofit2.http.Header
import retrofit2.http.POST

interface CartApi {
    @POST("shoppingCart")
    fun cart(
        @Header("Authorization") Authorization:String,
        @Header("lang") lang:String= Helper.language
    ): Observable<Response<ShopingModel>>
    @FormUrlEncoded
    @POST("removeCartItems")
    fun removeItem(
        @Field("item_ids[]") ids:MutableList<Int>,
        @Header("Authorization") Authorization:String,
        @Header("lang") lang:String= Helper.language
    ): Observable<Response<BaseResponse>>

    @FormUrlEncoded
    @POST("cartConfirmOrder")
    fun cartConfirmOrder(
        @Field("items") items:String,
        @Field("coupon_discount") coupon_discount:String?=null,
        @Header("Authorization") Authorization:String,
        @Header("lang") lang:String= Helper.language
    ):Observable<Response<BaseResponse>>

    @FormUrlEncoded
    @POST("addCoupon")
    fun addCoupon(
        @Field("coupon") coupon:String,
        @Header("Authorization") Authorization:String,
        @Header("lang") lang:String= Helper.language
    ):Observable<Response<BaseResponse>>
    @FormUrlEncoded
    @POST("removePostFromCart")
    fun removePostCart(
        @Field("post_id") post_id:String,
        @Header("Authorization") Authorization:String,
        @Header("lang") lang:String= Helper.language
    ):Observable<Response<BaseResponse>>
}