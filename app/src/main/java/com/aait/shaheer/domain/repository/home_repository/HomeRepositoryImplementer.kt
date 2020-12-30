package com.aait.shaheer.domain.repository.home_repository

import com.aait.shaheer.data_layer.cache.PreferencesGateway
import com.aait.shaheer.data_layer.cache.preferencesGateway
import com.aait.shaheer.data_layer.model.common.BaseResponse
import com.aait.shaheer.data_layer.model.connections_model.ConnectionsModel
import com.aait.shaheer.data_layer.model.follow_model.FollowModel
import com.aait.shaheer.data_layer.model.home_model.HomeModel
import com.aait.shaheer.data_layer.model.profile_model.ProfileModel
import com.aait.shaheer.data_layer.model.profile_model.my_following.MyFollowingsModel
import com.aait.shaheer.data_layer.model.register_model.RegisterModel
import com.aait.shaheer.data_layer.model.service_model.ServiceModel
import com.aait.shaheer.data_layer.model.stories_model.StoriesModel
import com.aait.shaheer.data_layer.remote.AuthApi
import com.aait.shaheer.data_layer.remote.HomeApi
import com.aait.shaheer.domain.repository.auth_repository.AuthRepository
import com.aait.shaheer.domain.repository.auth_repository.prefKeys
import com.kareem.datalayer.model.common.UserModel
import homeGateway
import io.reactivex.Flowable
import io.reactivex.Observable
import io.reactivex.Single
import okhttp3.MultipartBody
import retrofit2.Response

class HomeRepositoryImplementer(
    private val homeApi: HomeApi = homeGateway,
    private val preferences: PreferencesGateway = preferencesGateway
) : HomeRepository {
    override fun getSuggestdUsers(): Observable<Response<ConnectionsModel>> {
        return homeApi.suggested_friends(loadToken().blockingGet())
    }

    override fun hasFriends(): Single<Boolean> {
        return preferences.isSaved(prefKeys.CONNECTIONS)
    }

    override fun hasFriends(isConnect: Boolean) {
        preferences.save(prefKeys.CONNECTIONS,isConnect).subscribe()
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

    override fun follow(profileId: String): Observable<Response<FollowModel>> {
        return homeApi.follow(profileId,loadToken().blockingGet())
    }

    override fun myFollowings(page: String): Flowable<Response<MyFollowingsModel>> {
        return homeApi.getMyFollowings(page,loadToken().blockingGet())
    }

    override fun myFollowers(page: String): Flowable<Response<MyFollowingsModel>> {
        return homeApi.getMyFollowers(page,loadToken().blockingGet())
    }

    override fun myProfile(page: String): Flowable<Response<ProfileModel>> {
        return homeApi.getMyProfile(page,loadToken().blockingGet())
    }

    override fun like(post_id: String): Observable<Response<BaseResponse>> {
        return homeApi.like(post_id,loadToken().blockingGet())
    }

    override fun retweet(post_id: String): Observable<Response<BaseResponse>> {
        return homeApi.retweet(post_id,loadToken().blockingGet())
    }

    override fun pin(post_id: String): Observable<Response<BaseResponse>> {
        return homeApi.pinPost(post_id,loadToken().blockingGet())

    }

    override fun delete(post_id: String): Observable<Response<BaseResponse>> {
        return homeApi.deletePost(post_id,loadToken().blockingGet())

    }

    override fun reportUser(
        profileId: String,
        comment: String
    ): Observable<Response<BaseResponse>> {
        return homeApi.reportUser(profileId,comment,loadToken().blockingGet())
    }

    override fun block(profileId: String): Observable<Response<BaseResponse>> {
        return homeApi.block(profileId,loadToken().blockingGet())
    }

    override fun mute(profileId: String): Observable<Response<BaseResponse>> {
        return homeApi.mute(profileId,loadToken().blockingGet())
    }

    override fun home(page: String): Flowable<Response<HomeModel>> {
        return homeApi.home(page, loadToken().blockingGet())
    }

    override fun createStory(imgs:List<MultipartBody.Part>): Observable<Response<BaseResponse>> {
        return homeApi.createStory(imgs,loadToken().blockingGet())
    }

    override fun stories(): Observable<Response<StoriesModel>> {
        return homeApi.stories(loadToken().blockingGet())
    }

    override fun deleteStory(storyId: Int): Observable<Response<BaseResponse>> {
        return homeApi.deleteStory(storyId,loadToken().blockingGet())
    }

    override fun storySeen(ids: MutableList<Int>): Observable<Response<BaseResponse>> {
        return homeApi.storySeen(ids,loadToken().blockingGet())
    }


    override fun hasSavedToken(): Single<Boolean> {
        return preferences.isSaved(prefKeys.TOKEN)
    }

}