package com.aait.shaheer.domain.repository.home_repository

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

val homeRepository by lazy { HomeRepositoryImplementer() }

interface HomeRepository {
    fun getSuggestdUsers(
    ):Observable<Response<ConnectionsModel>>
    fun hasFriends():Single<Boolean>
    fun hasFriends(isConnect: Boolean)
    fun loadUser(): UserModel?
    fun loadToken(): Single<String>
    fun hasSavedToken(): Single<Boolean>
    fun saveToken(userData: RegisterModel)
    fun follow(profileId:String):Observable<Response<FollowModel>>
    fun myFollowings(page: String):Flowable<Response<MyFollowingsModel>>
    fun myFollowers(page: String):Flowable<Response<MyFollowingsModel>>
    fun myProfile(page: String):Flowable<Response<ProfileModel>>
    fun like(post_id: String):Observable<Response<BaseResponse>>
    fun retweet(post_id: String):Observable<Response<BaseResponse>>
    fun pin(post_id: String):Observable<Response<BaseResponse>>
    fun delete(post_id: String):Observable<Response<BaseResponse>>
    fun reportUser(profileId: String,comment: String):Observable<Response<BaseResponse>>
    fun block(profileId: String):Observable<Response<BaseResponse>>
    fun mute(profileId: String):Observable<Response<BaseResponse>>
    fun home(page: String):Flowable<Response<HomeModel>>
    fun createStory(imgs:List<MultipartBody.Part>):Observable<Response<BaseResponse>>
    fun stories(): Observable<Response<StoriesModel>>
    fun deleteStory(storyId:Int):Observable<Response<BaseResponse>>
    fun storySeen(ids:MutableList<Int>):Observable<Response<BaseResponse>>


}
