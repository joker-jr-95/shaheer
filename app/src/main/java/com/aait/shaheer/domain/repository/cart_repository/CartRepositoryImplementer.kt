package com.aait.shaheer.domain.repository.cart_repository

import cartGateway
import com.aait.shaheer.data_layer.cache.PreferencesGateway
import com.aait.shaheer.data_layer.cache.preferencesGateway
import com.aait.shaheer.data_layer.model.ItemCartItem
import com.aait.shaheer.data_layer.model.common.BaseResponse
import com.aait.shaheer.data_layer.model.shoping_model.ShopingModel
import com.aait.shaheer.data_layer.remote.CartApi
import com.aait.shaheer.domain.repository.base_repository.BaseRepositoryImplementer
import io.reactivex.Observable
import retrofit2.Response

class CartRepositoryImplementer(private val cartApi: CartApi = cartGateway,
                                private val preferences: PreferencesGateway = preferencesGateway
):CartRepository,BaseRepositoryImplementer(preferences) {
    override fun getCart(): Observable<Response<ShopingModel>> {
        return cartApi.cart(loadToken().blockingGet())
    }

    override fun removeItems(ids: MutableList<Int>): Observable<Response<BaseResponse>> {
        return cartApi.removeItem(ids,loadToken().blockingGet())
    }

    override fun removePostCart(post_id: Int): Observable<Response<BaseResponse>> {
        return cartApi.removePostCart(post_id.toString(),loadToken().blockingGet())
    }

    override fun cartConfirmOrder(
        items: String,
        coupon_discount: String?
    ): Observable<Response<BaseResponse>> {
        return cartApi.cartConfirmOrder(items,coupon_discount,loadToken().blockingGet())

    }

    override fun addCoupon(coupon: String): Observable<Response<BaseResponse>> {
        return cartApi.addCoupon(coupon,loadToken().blockingGet())
    }


}