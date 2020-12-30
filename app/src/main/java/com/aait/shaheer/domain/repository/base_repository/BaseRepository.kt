package com.aait.shaheer.domain.repository.base_repository

import com.aait.shaheer.data_layer.model.register_model.RegisterModel
import com.kareem.datalayer.model.common.UserModel
import io.reactivex.Single

val baseRepository by lazy { BaseRepositoryImplementer() }

interface BaseRepository{
    fun isLogin(): Boolean
    fun saveLogin(session:Boolean)
    fun loadUser(): UserModel?
    fun loadToken(): Single<String>
    fun hasSavedToken(): Single<Boolean>
    fun saveToken(userData: RegisterModel)
    var deviceId: String
    var lang: String

}