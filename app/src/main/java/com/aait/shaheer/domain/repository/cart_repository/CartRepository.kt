package com.aait.shaheer.domain.repository.cart_repository

import com.aait.shaheer.data_layer.model.ItemCartItem
import com.aait.shaheer.data_layer.model.common.BaseResponse
import com.aait.shaheer.data_layer.model.shoping_model.ShopingModel
import io.reactivex.Observable
import retrofit2.Response


val cartRepository by lazy { CartRepositoryImplementer() }

interface CartRepository{
    fun getCart():Observable<Response<ShopingModel>>
    fun removeItems(ids:MutableList<Int>):Observable<Response<BaseResponse>>
    fun removePostCart(post_id:Int):Observable<Response<BaseResponse>>
    fun cartConfirmOrder(/*items:MutableList<ItemCartItem>*/items:String,coupon_discount:String?=null):Observable<Response<BaseResponse>>
    fun addCoupon(coupon:String):Observable<Response<BaseResponse>>
}