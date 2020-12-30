package com.aait.shaheer.domain.repository.post_repository

import com.aait.shaheer.data_layer.model.add_cart_model.AddCartModel
import com.aait.shaheer.data_layer.model.common.BaseResponse
import com.aait.shaheer.data_layer.model.post_comment_model.PostCommentModel
import com.aait.shaheer.data_layer.model.post_model.PostModel
import com.aait.shaheer.data_layer.model.register_model.RegisterModel
import com.aait.shaheer.domain.repository.home_repository.HomeRepositoryImplementer
import com.kareem.datalayer.model.common.UserModel
import io.reactivex.Observable
import io.reactivex.Single
import okhttp3.MultipartBody
import retrofit2.Response
import retrofit2.http.Part
import retrofit2.http.Query

val postRepository by lazy { PostRepositoryImplementer() }

interface PostRepository {
    fun loadUser(): UserModel?
    fun loadToken(): Single<String>
    fun hasSavedToken(): Single<Boolean>
    fun saveToken(userData: RegisterModel)

    fun like(comment_id:String): Observable<Response<BaseResponse>>
    fun reply_like(comment_id:String):Observable<Response<BaseResponse>>
    fun addComment(post_id:String,comment:String,
                   image:MultipartBody.Part?=null):Observable<Response<PostCommentModel>>
    fun getComments(post_id:String):Observable<Response<PostCommentModel>>
    fun commentReplies(comment_id:String):Observable<Response<PostCommentModel>>
    fun addReply(
        comment_id:String,
        reply:String,
        image: MultipartBody.Part?
    ):Observable<Response<PostCommentModel>>

    fun post(post_id:String):Observable<Response<PostModel>>
    fun addToCart(
        post_id:String,
        choose_call:Boolean?=false,
        choose_video:Boolean?=false
    ):Observable<Response<AddCartModel>>

    fun bookMarked(
        post_id:String
    ):Observable<Response<AddCartModel>>

    fun addPost(
        catId:String,
        content:String,
        price:String,
        call_price:String,
        video_price:String,
        deadline:String,
        images:List<MultipartBody.Part>?=null
    ):Observable<Response<BaseResponse>>

}