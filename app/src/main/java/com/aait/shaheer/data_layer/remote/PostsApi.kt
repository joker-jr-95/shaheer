package com.aait.shaheer.data_layer.remote

import com.aait.shaheer.data_layer.model.add_cart_model.AddCartModel
import com.aait.shaheer.data_layer.model.common.BaseResponse
import com.aait.shaheer.data_layer.model.post_comment_model.PostCommentModel
import com.aait.shaheer.data_layer.model.post_model.PostModel
import com.aait.shaheer.data_layer.model.register_model.RegisterModel
import io.reactivex.Flowable
import io.reactivex.Observable
import okhttp3.MultipartBody
import retrofit2.Response
import retrofit2.http.*

interface PostsApi {
    @POST("postComments")
    fun comments(
        @Query("post_id") post_id:String,
        @Header("Authorization") token:String?=null,
        @Header("lang") lang:String= Helper.language
    ): Observable<Response<PostCommentModel>>

    @POST("likeComment")
    fun like(
        @Query("comment_id") comment_id:String,
        @Header("Authorization") token:String?=null,
        @Header("lang") lang:String= Helper.language
    ): Observable<Response<BaseResponse>>

    @POST("likeReply")
    fun like_reply(
        @Query("reply_id") reply_id:String,
        @Header("Authorization") token:String?=null,
        @Header("lang") lang:String= Helper.language
    ): Observable<Response<BaseResponse>>

    @Multipart
    @POST("createComment")
    fun addComment(
        @Query("post_id") post_id:String,
        @Query("comment") comment:String,
        @Part image:MultipartBody.Part,
        @Header("Authorization") token:String?=null,
        @Header("lang") lang:String= Helper.language
    ): Observable<Response<PostCommentModel>>

    @POST("createComment")
    fun addComment(
        @Query("post_id") post_id:String,
        @Query("comment") comment:String,
        @Header("Authorization") token:String?=null,
        @Header("lang") lang:String= Helper.language
    ): Observable<Response<PostCommentModel>>

    @POST("createReply")
    fun reply(
        @Query("comment_id") comment_id:String,
        @Query("reply") reply:String,
        @Header("Authorization") token:String?=null,
        @Header("lang") lang:String= Helper.language
    ): Observable<Response<PostCommentModel>>

    @Multipart
    @POST("createReply")
    fun reply(
        @Query("comment_id") comment_id:String,
        @Query("reply") reply:String,
        @Part image:MultipartBody.Part,
        @Header("Authorization") token:String?=null,
        @Header("lang") lang:String= Helper.language
    ): Observable<Response<PostCommentModel>>

    @POST("commentReplies")
    fun commentReplies(
        @Query("comment_id") comment_id:String,
        @Header("Authorization") token:String?=null,
        @Header("lang") lang:String= Helper.language
    ): Observable<Response<PostCommentModel>>

    @POST("showPost")
    fun showPost(
        @Query("post_id") post_id:String,
        @Header("Authorization") token:String?=null,
        @Header("lang") lang:String= Helper.language
    ): Observable<Response<PostModel>>

    @POST("bookmarkPost")
    fun bookmarkPost(
        @Query("post_id") post_id:String,
        @Header("Authorization") token:String?=null,
        @Header("lang") lang:String= Helper.language
    ): Observable<Response<AddCartModel>>


    @POST("addTocart")
    fun addTocart(
        @Query("post_id") post_id:String,
        @Query("choose_call") choose_call:Boolean?=false,
        @Query("choose_video") choose_video:Boolean?=false,
        @Header("Authorization") token:String?=null,
        @Header("lang") lang:String= Helper.language
    ): Observable<Response<AddCartModel>>

    @Multipart
    @POST("createPost")
    fun addPost(
        @Query("category_id") category_id:String,
        @Query("content") content:String,
        @Query("price") price:String,
        @Query("call_price") call_price:String,
        @Query("video_price") video_price:String,
        @Query("deadline") deadline:String,
        @Part images:List<MultipartBody.Part>,
        @Header("Authorization") token:String?=null,
        @Header("lang") lang:String= Helper.language
    ):Observable<Response<BaseResponse>>

    @POST("createPost")
    fun addPost(
        @Query("category_id") category_id:String,
        @Query("content") content:String,
        @Query("price") price:String,
        @Query("call_price") call_price:String,
        @Query("video_price") video_price:String,
        @Query("deadline") deadline:String,
        @Header("Authorization") token:String?=null,
        @Header("lang") lang:String= Helper.language
    ):Observable<Response<BaseResponse>>


}