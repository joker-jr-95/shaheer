package com.aait.shaheer.domain.repository.profile_repository

import com.aait.shaheer.data_layer.model.categories_model.CategoriesModel
import com.aait.shaheer.data_layer.model.profile_model.ProfileModel
import com.aait.shaheer.data_layer.model.profile_model.my_following.MyFollowingsModel
import com.aait.shaheer.data_layer.model.register_model.RegisterModel
import com.aait.shaheer.domain.repository.post_repository.PostRepositoryImplementer
import com.kareem.datalayer.model.common.UserModel
import io.reactivex.Flowable
import io.reactivex.Observable
import io.reactivex.Single
import okhttp3.MultipartBody
import retrofit2.Response

val profileRepository by lazy { ProfileRepositoryImplementer() }

interface ProfileRepository {
    fun loadUser(): UserModel?
    fun loadToken(): Single<String>
    fun saveUser(userData:RegisterModel)
    fun saveToken(userData:RegisterModel)
    fun hasSavedToken(): Single<Boolean>
    fun getCategories():Observable<Response<CategoriesModel>>
    fun editProfile(
        phone:String,
        email:String,
        name:String,
        category_id:String?=null,
        gender:String?=null,
        bio:String?=null,
        avatar: MultipartBody.Part?=null,
        cover: MultipartBody.Part?=null
    ):Observable<Response<RegisterModel>>

    fun getProfile(
        profile_id:String,
        page:String?=null
    ):Observable<Response<ProfileModel>>

    fun getProfileFlow(
        profile_id:String,
        page:String?=null
    ):Flowable<Response<ProfileModel>>

    fun followings(
        profile_id:String,
        page:String?=null
    ):Flowable<Response<MyFollowingsModel>>
    fun followers(
        profile_id:String,
        page:String?=null
    ):Flowable<Response<MyFollowingsModel>>

}