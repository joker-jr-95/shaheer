package com.aait.shaheer.app

import android.annotation.SuppressLint
import android.app.Application
import android.content.Context
import android.os.Build
import android.util.Log
import com.aait.shaheer.data_layer.cache.preferencesGateway
import com.aait.shaheer.helper.DomainIntegration
import com.kareem.domain.core.Util

class AppController : Application() {

    override fun onCreate() {
        super.onCreate()
        Log.e("MyApp","0")
        DomainIntegration.with(applicationContext)


    }

    @SuppressLint("CheckResult")
    override fun attachBaseContext(newBase: Context) {
        DomainIntegration.with(newBase)
        if (Build.VERSION.SDK_INT > Build.VERSION_CODES.N_MR1) {
            Log.e("myApp","1")
            if (Util.language=="ar"){
                preferencesGateway.save("lang","ar")
            }
            else{
                preferencesGateway.save("lang","en")
            }
            val pref = preferencesGateway.load("lang", "ar")
            super.attachBaseContext( Util.changeLang(pref.blockingGet(),newBase))
        }
        else {
            Log.e("myApp","4")
            super.attachBaseContext(newBase)
            //super.attachBaseContext(CalligraphyContextWrapper.wrap(newBase));
        }
    }
}