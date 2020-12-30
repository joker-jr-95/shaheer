package com.aait.shaheer.domain.repository.auth_repository

import com.aait.shaheer.data_layer.model.common.BaseResponse
import com.aait.shaheer.data_layer.model.register_model.RegisterModel
import com.kareem.datalayer.model.common.UserModel
import io.reactivex.Observable
import io.reactivex.Single
import retrofit2.Response
import retrofit2.http.Query

val authRepository by lazy { AuthenticationRepositoryImplementer() }

interface AuthRepository {

    fun register(userName:String,
        phoneNo:String?=null,
        password:String?=null,
        email:String?=null,
        country_iso:String?=null,
        devId:String?=null
    ): Observable<Response<RegisterModel>>

    fun login(email_phoneNo:String,password:String,devId:String?=null): Observable<Response<RegisterModel>>
    fun saveUser(userData:RegisterModel)
    fun resetPassword(
        code:String,
        password:String
    ):Observable<Response<RegisterModel>>

    fun forgetPass(email_phoneNo:String): Observable<Response<RegisterModel>>
    fun confirmPass(newPassword:String,user_id:String): Observable<Response<RegisterModel>>
    fun resendCode():Observable<Response<RegisterModel>>
    fun checkCode(code:String): Observable<Response<RegisterModel>>
    fun activeAccount(code:String): Observable<Response<RegisterModel>>
    fun isLogin(): Boolean
    fun saveLogin(session:Boolean)
    fun loadUser(): UserModel?
    fun loadToken():Single<String>
    fun hasSavedToken(): Single<Boolean>
    fun saveToken(userData:RegisterModel)
    fun updateFcm(old_device_id:String,
                  new_device_id:String):Observable<Response<BaseResponse>>


}