package com.aait.shaheer.data_layer.remote

import com.aait.shaheer.data_layer.model.add_cart_model.AddCartModel
import com.aait.shaheer.data_layer.model.categories_model.CategoriesModel
import com.aait.shaheer.data_layer.model.profile_model.ProfileModel
import com.aait.shaheer.data_layer.model.profile_model.my_following.MyFollowingsModel
import com.aait.shaheer.data_layer.model.register_model.RegisterModel
import com.aait.shaheer.domain.repository.base_repository.baseRepository
import io.reactivex.Flowable
import io.reactivex.Observable
import okhttp3.MultipartBody
import retrofit2.Response
import retrofit2.http.*

interface ProfileApi {

    @POST("categories")
    fun categories(
        @Header("lang") lang:String= Helper.language
    ): Observable<Response<CategoriesModel>>

    @FormUrlEncoded
    @POST("editProfile")
    fun editProfile(
        @Field("phone") phone:String,
        @Field("email") email:String,
        @Field("name") name:String,
        @Field("category_id") category_id:String?=null,
        @Field("gender") gender:String?=null,
        @Field("bio") bio:String?=null,
        @Field("address") address:String?=null,
        @Field("lat") lat:String?=null,
        @Field("lng") lng:String?=null,
        @Header("Authorization") token:String?=null,
        @Header("lang") lang:String= baseRepository.lang
    ): Observable<Response<RegisterModel>>

    @Multipart
    @POST("editProfile")
    fun editProfile(
        @Query("phone") phone:String,
        @Query("email") email:String,
        @Query("name") name:String,
        @Query("category_id") category_id:String?=null,
        @Query("gender") gender:String?=null,
        @Query("bio") bio:String?=null,
        @Part avatar:MultipartBody.Part?=null,
        @Part cover:MultipartBody.Part?=null,
        @Query("address") address:String?=null,
        @Query("lat") lat:String?=null,
        @Query("lng") lng:String?=null,
        @Header("Authorization") token:String?=null,
        @Header("lang") lang:String= Helper.language
    ): Observable<Response<RegisterModel>>

    @POST("userProfile")
    fun profile(
        @Query("page") page:String?=null,
        @Query("profile_id") profile_id:String,
        @Header("Authorization") token:String?=null,
        @Header("lang") lang:String= Helper.language

    ):Observable<Response<ProfileModel>>
    @POST("userProfile")
    fun profileFlow(
        @Query("page") page:String?=null,
        @Query("profile_id") profile_id:String,
        @Header("Authorization") token:String?=null,
        @Header("lang") lang:String= Helper.language

    ):Flowable<Response<ProfileModel>>

    @POST("userFollowings")
    fun followings(
        @Query("page") page:String?=null,
        @Query("profile_id") profile_id:String,
        @Header("Authorization") token:String?=null,
        @Header("lang") lang:String= Helper.language
    ):Flowable<Response<MyFollowingsModel>>

    @POST("userFollowers")
    fun followers(
        @Query("page") page:String?=null,
        @Query("profile_id") profile_id:String,
        @Header("Authorization") token:String?=null,
        @Header("lang") lang:String= Helper.language
    ):Flowable<Response<MyFollowingsModel>>


}