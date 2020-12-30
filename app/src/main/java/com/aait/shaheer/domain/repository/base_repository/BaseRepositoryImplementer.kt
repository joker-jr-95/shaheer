package com.aait.shaheer.domain.repository.base_repository

import com.aait.shaheer.data_layer.cache.PreferencesGateway
import com.aait.shaheer.data_layer.cache.preferencesGateway
import com.aait.shaheer.data_layer.model.register_model.RegisterModel
import com.aait.shaheer.data_layer.remote.CartApi
import com.aait.shaheer.domain.repository.auth_repository.prefKeys
import com.aait.shaheer.domain.repository.cart_repository.CartRepository
import com.google.firebase.iid.FirebaseInstanceId
import com.kareem.datalayer.model.common.UserModel
import com.kareem.domain.core.Util
import io.reactivex.Single


open class BaseRepositoryImplementer(
    private val preferences: PreferencesGateway = preferencesGateway
): BaseRepository{

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

    override var deviceId: String
        get() = preferences.load(prefKeys.FIREBASE,"").blockingGet()
        set(value) {
            preferences.save(prefKeys.FIREBASE,value).subscribe()
        }
    override var lang: String
        get() = preferences.load(prefKeys.LANG, "ar").blockingGet()
        set(value) {
            preferences.save(prefKeys.LANG,value).subscribe()
        }

    override fun hasSavedToken(): Single<Boolean> {
        return preferences.isSaved(prefKeys.TOKEN)
    }

    override fun isLogin(): Boolean {
        return preferences.load(prefKeys.SESSION,false).blockingGet()
    }

    override fun saveLogin(session: Boolean) {
        preferences.save(prefKeys.SESSION,session).subscribe()
    }

}