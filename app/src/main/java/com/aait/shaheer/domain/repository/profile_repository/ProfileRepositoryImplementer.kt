package com.aait.shaheer.domain.repository.profile_repository

import android.annotation.SuppressLint
import com.aait.shaheer.data_layer.cache.PreferencesGateway
import com.aait.shaheer.data_layer.cache.preferencesGateway
import com.aait.shaheer.data_layer.model.categories_model.CategoriesModel
import com.aait.shaheer.data_layer.model.profile_model.ProfileModel
import com.aait.shaheer.data_layer.model.profile_model.my_following.MyFollowingsModel
import com.aait.shaheer.data_layer.model.register_model.RegisterModel
import com.aait.shaheer.data_layer.remote.HomeApi
import com.aait.shaheer.data_layer.remote.ProfileApi
import com.aait.shaheer.domain.repository.auth_repository.prefKeys
import com.aait.shaheer.domain.repository.home_repository.HomeRepository
import com.kareem.datalayer.model.common.UserModel
import io.reactivex.Flowable
import io.reactivex.Observable
import io.reactivex.Single
import okhttp3.MultipartBody
import profileGateway
import retrofit2.Response

class ProfileRepositoryImplementer(
    private val profileApi: ProfileApi = profileGateway,
    private val preferences: PreferencesGateway = preferencesGateway
) : ProfileRepository {
    override fun getCategories(): Observable<Response<CategoriesModel>> {
        return profileApi.categories()
    }

    override fun editProfile(
        phone:String,
        email: String,
        name: String,
        category_id: String?,
        gender: String?,
        bio: String?,
        avatar: MultipartBody.Part?,
        cover: MultipartBody.Part?
    ): Observable<Response<RegisterModel>> {
        if (avatar ==null && cover ==null) {
            return profileApi
                .editProfile(phone,email, name, category_id, gender, bio,token = loadToken().blockingGet())
        }
        else{
            return profileApi.editProfile(
                phone, email, name, category_id, gender, bio, avatar, cover,token = loadToken().blockingGet()
            )
        }
    }

    override fun getProfile(profile_id: String, page: String?): Observable<Response<ProfileModel>> {
        return profileApi.profile(page, profile_id,loadToken().blockingGet())
    }

    override fun getProfileFlow(
        profile_id: String,
        page: String?
    ): Flowable<Response<ProfileModel>> {
        return profileApi.profileFlow(page, profile_id,loadToken().blockingGet())
    }

    override fun followings(
        profile_id: String,
        page: String?
    ): Flowable<Response<MyFollowingsModel>> {
        return profileApi.followings(page, profile_id,loadToken().blockingGet())
    }

    override fun followers(
        profile_id: String,
        page: String?
    ): Flowable<Response<MyFollowingsModel>> {
        return profileApi.followers(page, profile_id,loadToken().blockingGet())
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

    @SuppressLint("CheckResult")
    override fun saveUser(resp: RegisterModel) {

        resp.data.also {
            preferences.save(prefKeys.USER,it!!).subscribe()
        }

    }

    override fun hasSavedToken(): Single<Boolean> {
        return preferences.isSaved(prefKeys.TOKEN)
    }

}