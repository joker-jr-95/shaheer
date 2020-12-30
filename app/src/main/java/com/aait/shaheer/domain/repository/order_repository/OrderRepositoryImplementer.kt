package com.aait.shaheer.domain.repository.order_repository

import com.aait.shaheer.data_layer.cache.PreferencesGateway
import com.aait.shaheer.data_layer.cache.preferencesGateway
import com.aait.shaheer.data_layer.model.collection_model.CollectionModel
import com.aait.shaheer.data_layer.model.common.BaseResponse
import com.aait.shaheer.data_layer.model.register_model.RegisterModel
import com.aait.shaheer.data_layer.model.service_model.ServiceModel
import com.aait.shaheer.data_layer.remote.HomeApi
import com.aait.shaheer.data_layer.remote.OrderApi
import com.aait.shaheer.domain.repository.auth_repository.prefKeys
import com.kareem.datalayer.model.common.UserModel
import io.reactivex.Observable
import io.reactivex.Single
import orderGateway
import retrofit2.Response

class OrderRepositoryImplementer(
    private val orderApi: OrderApi = orderGateway,
    private val preferences: PreferencesGateway = preferencesGateway
) :OrderRepository {
    override fun acceptOrder(orderId: String): Observable<Response<BaseResponse>> {
        return orderApi.acceptOrder(orderId,loadToken().blockingGet())
    }

    override fun refuseOrder(orderId: String): Observable<Response<BaseResponse>> {
        return orderApi.refuseOrder(orderId,loadToken().blockingGet())
    }

    override fun soldOrders(): Observable<Response<ServiceModel>> {
        return orderApi.soldOrders(loadToken().blockingGet())
    }

    override fun boughtOrders(): Observable<Response<ServiceModel>> {
        return orderApi.boughtOrders(loadToken().blockingGet())
    }

    override fun collections(): Observable<Response<CollectionModel>> {
        return orderApi.collections(loadToken().blockingGet())
    }

    override fun deleteOrder(orderId: String): Observable<Response<BaseResponse>> {
        return orderApi.deleteOrder(orderId,loadToken().blockingGet())
    }

    override fun rateOrder(
        post_id: String,
        user_id: String,
        rate: String,
        comment: String
    ): Observable<Response<BaseResponse>> {
        return orderApi.rate(post_id, user_id, rate, comment,loadToken().blockingGet())
    }

    override fun closeSoldOrder(orderId: String,close_reason:String): Observable<Response<BaseResponse>> {
        return orderApi.closeSoldOrder(orderId,close_reason,loadToken().blockingGet())
    }

    override fun closeBoughtOrder(orderId: String, close_reason: String): Observable<Response<BaseResponse>> {
       return orderApi.closeBoughtOrder(orderId,close_reason,loadToken().blockingGet())
    }

    override fun finishOrder(orderId: String): Observable<Response<BaseResponse>> {
        return orderApi.finishOrder(orderId,loadToken().blockingGet())
    }

    override fun reportOrder(order_id: String,subject:String,reason:String): Observable<Response<BaseResponse>> {
        return orderApi.reportOrder(order_id,subject,reason,loadToken().blockingGet())
    }


    override fun hasSavedToken(): Single<Boolean> {
        return preferences.isSaved(prefKeys.TOKEN)
    }


    override fun loadToken(): Single<String> {
        return preferences.load(prefKeys.TOKEN,"")
    }


    override fun loadUser(): UserModel? {
        if (preferences.isSaved(prefKeys.USER).blockingGet()) {
            val also = preferences.load(
                prefKeys.USER,
                UserModel()
            ).filter {
                it != UserModel()
            }.also { it.subscribe() }
            return also.blockingGet()
        }
        else{
            return null
        }
    }

    override fun saveToken(userData: RegisterModel) {
        userData.data?.token!!.also {
            preferences.save(prefKeys.TOKEN,"Bearer $it").subscribe()
        }
    }

}