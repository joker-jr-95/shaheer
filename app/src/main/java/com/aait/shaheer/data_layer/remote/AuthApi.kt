package com.aait.shaheer.data_layer.remote

import com.aait.shaheer.data_layer.model.common.BaseResponse
import com.aait.shaheer.data_layer.model.register_model.RegisterModel
import com.aait.shaheer.data_layer.remote.Helper
import com.google.firebase.iid.FirebaseInstanceId
import io.reactivex.Observable
import retrofit2.Response
import retrofit2.http.*

interface AuthApi {
    @FormUrlEncoded
    @POST("signUp")
    fun register(
        @Field("name") userName:String,
        @Field("phone") phoneNo:String?=null,
        @Field("password") password:String?=null,
        @Field("email") email:String?=null,
        @Field("country_iso") country_iso:String?=null,
        @Field("device_id") deviceID:String?=FirebaseInstanceId.getInstance().token/*FirebaseInstanceId.getInstance().Authorization*/,
        @Field("device_type") deviceType:String="android",
        @Header("lang") lang:String= Helper.language
    ): Observable<Response<RegisterModel>>

    @FormUrlEncoded
    @POST("signIn")
    fun login(
        @Field("phone") userName:String,
        @Field("password") password:String,
        @Field("device_id") deviceID:String?=FirebaseInstanceId.getInstance().token/*FirebaseInstanceId.getInstance().Authorization*/,
        @Field("device_type") deviceType:String="android",
        @Header("lang") lang:String= Helper.language
    ): Observable<Response<RegisterModel>>

    @FormUrlEncoded
    @POST("forgetPassword")
    fun forgetPassword(
        @Field("phone") phone:String,
        @Header("Authorization") Authorization:String,
        @Header("lang") lang:String= Helper.language
    ): Observable<Response<RegisterModel>>
    //doesn't return data
    @FormUrlEncoded
    @POST("checkCode")
    fun checkCode(
        @Field("activitionCode") activitionCode:String,
        @Header("Authorization") Authorization:String,
        @Header("lang") lang:String= Helper.language
    ): Observable<Response<RegisterModel>>
    //doesn't return data
    @FormUrlEncoded
    @POST("confirmPassword")
    fun confirmPass(
        @Query("activitionCode") activitionCode:String?=null,
        @Field("newPassword") newPassword:String,
        @Header("Authorization") Authorization:String,
        @Header("lang") lang:String= Helper.language
    ): Observable<Response<RegisterModel>>

    @FormUrlEncoded
    @POST("accountActivation")
    fun activeAccount(
        @Field("code") code:String,
        @Header("Authorization") Authorization:String,
        @Header("lang") lang:String= Helper.language
    ):Observable<Response<RegisterModel>>

    @POST("sendActivation")
    fun resendActivition(
        @Header("Authorization") Authorization:String,
        @Header("lang") lang:String= Helper.language
    ): Observable<Response<RegisterModel>>

    @POST("resetPassword")
    fun resetPass(
        @Query("code") code:String,
        @Query("password") password:String,
        @Header("Authorization") Authorization:String,
        @Header("lang") lang:String= Helper.language
        ):Observable<Response<RegisterModel>>
    
    @POST("updateDeviceId")
    fun updateDeviceFcm(
        @Query("old_device_id") old_device_id:String,
        @Query("new_device_id") new_device_id:String,
        @Header("Authorization") Authorization:String,
        @Header("lang") lang:String= Helper.language
        ):Observable<Response<BaseResponse>>
}

