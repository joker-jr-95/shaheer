package com.aait.shaheer.data_layer.remote

import com.aait.shaheer.data_layer.model.intro_model.IntroModel
import io.reactivex.Observable
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.POST

interface IntroApi {
    @GET("sliders")
    fun getSliders():Observable<Response<IntroModel>>
}