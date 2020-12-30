package com.aait.shaheer.domain.repository.auth_repository
import android.annotation.SuppressLint
import authGateway
import com.aait.shaheer.data_layer.cache.PreferencesGateway
import com.aait.shaheer.data_layer.cache.preferencesGateway
import com.aait.shaheer.data_layer.model.common.BaseResponse
import com.aait.shaheer.data_layer.model.register_model.RegisterModel
import com.kareem.datalayer.model.common.UserModel
import com.aait.shaheer.data_layer.remote.AuthApi
import com.aait.shaheer.domain.repository.auth_repository.prefKeys.SESSION
import com.aait.shaheer.domain.repository.auth_repository.prefKeys.TOKEN
import com.aait.shaheer.domain.repository.auth_repository.prefKeys.USER
import io.reactivex.Observable
import io.reactivex.Single
import retrofit2.Response

class AuthenticationRepositoryImplementer(
    private val userApi: AuthApi = authGateway,
    private val preferences: PreferencesGateway = preferencesGateway
) : AuthRepository {
    override fun register(
        userName: String,
        phoneNo: String?,
        password: String?,
        email: String?,
        iso:String?,
        devId:String?
    ): Observable<Response<RegisterModel>> {
        return userApi.register(userName,phoneNo, password, email,iso,devId)
    }

    override fun login(
        email_phoneNo: String,
        password: String,
        devId:String?
    ): Observable<Response<RegisterModel>> {
        return userApi.login(email_phoneNo,password,devId)
    }

    @SuppressLint("CheckResult")
    override fun saveUser(resp: RegisterModel) {

            resp.data.also {
                preferences.save(USER,it!!).subscribe()
            }

    }

    override fun resetPassword(
        code: String,
        password: String
    ): Observable<Response<RegisterModel>> {
        return userApi.resetPass(code,password
            ,loadToken().blockingGet())
    }

    override fun loadToken(): Single<String> {
        return preferences.load(TOKEN,"")
    }


    override fun loadUser(): UserModel? {
        if (preferences.isSaved(USER).blockingGet()) {
            val also = preferences.load(
                USER,
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
            preferences.save(TOKEN,"Bearer $it").subscribe()
        }
    }

    override fun updateFcm(old_device_id: String, new_device_id: String): Observable<Response<BaseResponse>> {
        return userApi.updateDeviceFcm(old_device_id,new_device_id,loadToken().blockingGet())
    }

    override fun hasSavedToken(): Single<Boolean> {
        return preferences.isSaved(TOKEN)
    }

    override fun forgetPass(email_phoneNo: String): Observable<Response<RegisterModel>> {
        return userApi.forgetPassword(email_phoneNo,Authorization=loadToken().blockingGet())
    }

    override fun confirmPass(
        newPassword: String,
        user_id: String
    ): Observable<Response<RegisterModel>> {
        return userApi.confirmPass(newPassword = newPassword,Authorization = loadToken().blockingGet())
    }

    override fun resendCode(): Observable<Response<RegisterModel>> {
        return userApi.resendActivition(loadToken().blockingGet())
    }

    override fun checkCode( code: String): Observable<Response<RegisterModel>> {
        return userApi.checkCode(code,loadToken().blockingGet())
    }

    override fun activeAccount( code: String): Observable<Response<RegisterModel>> {
        return userApi.activeAccount(code,loadToken().blockingGet())
    }

    override fun isLogin(): Boolean {
        return preferences.load(SESSION,false).blockingGet()
    }

    override fun saveLogin(session: Boolean) {
         preferences.save(SESSION,session).subscribe()
    }

}
object prefKeys {
    val FIREBASE: String="fcm"
    val LANG: String="lang"
    val CONNECTIONS: String="connect"
    val TOKEN: String="token"
    val USER: String="user"
    val SESSION: String="session"
}