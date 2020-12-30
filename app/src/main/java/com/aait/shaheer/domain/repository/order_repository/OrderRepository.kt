package com.aait.shaheer.domain.repository.order_repository

import com.aait.shaheer.data_layer.model.collection_model.CollectionModel
import com.aait.shaheer.data_layer.model.common.BaseResponse
import com.aait.shaheer.data_layer.model.connections_model.ConnectionsModel
import com.aait.shaheer.data_layer.model.follow_model.FollowModel
import com.aait.shaheer.data_layer.model.home_model.HomeModel
import com.aait.shaheer.data_layer.model.profile_model.ProfileModel
import com.aait.shaheer.data_layer.model.profile_model.my_following.MyFollowingsModel
import com.aait.shaheer.data_layer.model.register_model.RegisterModel
import com.aait.shaheer.data_layer.model.service_model.ServiceModel
import com.aait.shaheer.data_layer.model.stories_model.StoriesModel
import com.aait.shaheer.data_layer.remote.Helper
import com.aait.shaheer.domain.repository.auth_repository.AuthenticationRepositoryImplementer
import com.kareem.datalayer.model.common.UserModel
import io.reactivex.Flowable
import io.reactivex.Observable
import io.reactivex.Single
import okhttp3.MultipartBody
import org.w3c.dom.Comment
import retrofit2.Response
import retrofit2.http.Header
import retrofit2.http.POST
import retrofit2.http.Query

val orderRepository by lazy { OrderRepositoryImplementer() }

interface OrderRepository {
    fun acceptOrder(orderId:String):Observable<Response<BaseResponse>>
    fun refuseOrder(orderId:String):Observable<Response<BaseResponse>>
    fun deleteOrder(orderId:String):Observable<Response<BaseResponse>>
    fun rateOrder(post_id:String,user_id:String,rate:String,comment:String):Observable<Response<BaseResponse>>
    fun closeSoldOrder(orderId:String,close_reason:String):Observable<Response<BaseResponse>>
    fun closeBoughtOrder(orderId:String,close_reason:String):Observable<Response<BaseResponse>>
    fun finishOrder(orderId:String):Observable<Response<BaseResponse>>
    fun reportOrder(order_id:String,subject:String,reason:String):Observable<Response<BaseResponse>>
    fun soldOrders():Observable<Response<ServiceModel>>
    fun boughtOrders():Observable<Response<ServiceModel>>
    fun collections():Observable<Response<CollectionModel>>

    fun loadUser(): UserModel?
    fun loadToken(): Single<String>
    fun hasSavedToken(): Single<Boolean>
    fun saveToken(userData: RegisterModel)
}
