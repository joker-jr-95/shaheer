package com.aait.shaheer.helper

import android.content.Context
import java.lang.ref.WeakReference


object DomainIntegration {

    private lateinit var applicationReference : WeakReference<Context>

    fun with(application: Context){
        applicationReference = WeakReference(application)
    }

    fun getApplication() = applicationReference.get()!!
}