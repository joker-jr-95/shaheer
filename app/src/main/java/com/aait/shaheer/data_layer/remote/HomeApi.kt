package com.aait.shaheer.data_layer.remote

import com.aait.shaheer.data_layer.model.common.BaseResponse
import com.aait.shaheer.data_layer.model.connections_model.ConnectionsModel
import com.aait.shaheer.data_layer.model.follow_model.FollowModel
import com.aait.shaheer.data_layer.model.home_model.HomeModel
import com.aait.shaheer.data_layer.model.profile_model.ProfileModel
import com.aait.shaheer.data_layer.model.profile_model.my_following.MyFollowingsModel
import com.aait.shaheer.data_layer.model.service_model.ServiceModel
import com.aait.shaheer.data_layer.model.stories_model.StoriesModel
import com.aait.shaheer.data_layer.remote.Helper
import io.reactivex.Flowable

import io.reactivex.Observable
import okhttp3.MultipartBody
import retrofit2.Response
import retrofit2.http.*

interface HomeApi {
    @POST("suggestUsers")
    fun suggested_friends(
        @Header("Authorization") token:String?=null,
        @Header("lang") lang:String= Helper.language
    ): Observable<Response<ConnectionsModel>>
    @POST("follow")
    fun follow(
        @Query("profile_id") profile_id:String,
        @Header("Authorization") token:String?=null,
        @Header("lang") lang:String= Helper.language
    ): Observable<Response<FollowModel>>
    @POST("myProfile")
    fun getMyProfile(
        @Query("page") page:String,
        @Header("Authorization") token:String?=null,
        @Header("lang") lang:String= Helper.language
    ):Flowable<Response<ProfileModel>>
    @POST("myFollowings")
    fun getMyFollowings(
        @Query("page") page:String,
        @Header("Authorization") token:String?=null,
        @Header("lang") lang:String= Helper.language
    ):Flowable<Response<MyFollowingsModel>>
    @POST("myFollowers")
    fun getMyFollowers(
        @Query("page") page:String,
        @Header("Authorization") token:String?=null,
        @Header("lang") lang:String= Helper.language
    ):Flowable<Response<MyFollowingsModel>>

    @POST("repost")
    fun retweet(
        @Query("post_id") profile_id:String,
        @Header("Authorization") token:String?=null,
        @Header("lang") lang:String= Helper.language
    ): Observable<Response<BaseResponse>>

    @POST("like")
    fun like(
        @Query("post_id") profile_id:String,
        @Header("Authorization") token:String?=null,
        @Header("lang") lang:String= Helper.language
    ): Observable<Response<BaseResponse>>

    @POST("pinPost")
    fun pinPost(
        @Query("post_id") post_id:String,
        @Header("Authorization") token:String?=null,
        @Header("lang") lang:String= Helper.language
    ): Observable<Response<BaseResponse>>

    @POST("deletePost")
    fun deletePost(
        @Query("post_id") post_id:String,
        @Header("Authorization") token:String?=null,
        @Header("lang") lang:String= Helper.language
    ): Observable<Response<BaseResponse>>

    @POST("blockUser")
    fun block(
        @Query("profile_id") post_id:String,
        @Header("Authorization") token:String?=null,
        @Header("lang") lang:String= Helper.language
    ): Observable<Response<BaseResponse>>

    @POST("reportUser")
    fun reportUser(
        @Query("profile_id") profile_id:String,
        @Query("content") content:String,
        @Header("Authorization") token:String?=null,
        @Header("lang") lang:String= Helper.language
    ): Observable<Response<BaseResponse>>
    @POST("muteUser")
    fun mute(
        @Query("profile_id") profile_id:String,
        @Header("Authorization") token:String?=null,
        @Header("lang") lang:String= Helper.language
    ): Observable<Response<BaseResponse>>

    @POST("home")
    fun home(
        @Query("page") page:String,
        @Header("Authorization") token:String?=null,
        @Header("lang") lang:String= Helper.language
    ):Flowable<Response<HomeModel>>

    @Multipart
    @POST("createStory")
    fun createStory(
        @Part images:List<MultipartBody.Part>,
        @Header("Authorization") token:String?=null,
        @Header("lang") lang:String= Helper.language
    ):Observable<Response<BaseResponse>>

    @POST("usersStories")
    fun stories(
        @Header("Authorization") token:String?=null,
        @Header("lang") lang:String= Helper.language
    ):Observable<Response<StoriesModel>>

    @FormUrlEncoded
    @POST("viewStories")
    fun storySeen(
        @Field("ids[]") ids:MutableList<Int>,
        @Header("Authorization") token:String?=null,
        @Header("lang") lang:String= Helper.language
    ):Observable<Response<BaseResponse>>


    @POST("deleteStory")
    fun deleteStory(
        @Query("story_id") story_id:Int,
        @Header("Authorization") token:String?=null,
        @Header("lang") lang:String= Helper.language
    ):Observable<Response<BaseResponse>>




}
