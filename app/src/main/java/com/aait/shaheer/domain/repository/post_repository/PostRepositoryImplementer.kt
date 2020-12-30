package com.aait.shaheer.domain.repository.post_repository

import com.aait.shaheer.data_layer.cache.PreferencesGateway
import com.aait.shaheer.data_layer.cache.preferencesGateway
import com.aait.shaheer.data_layer.model.add_cart_model.AddCartModel
import com.aait.shaheer.data_layer.model.common.BaseResponse
import com.aait.shaheer.data_layer.model.post_comment_model.PostCommentModel
import com.aait.shaheer.data_layer.model.post_model.PostModel
import com.aait.shaheer.data_layer.model.register_model.RegisterModel
import com.aait.shaheer.data_layer.remote.PostsApi
import com.aait.shaheer.domain.repository.auth_repository.prefKeys
import com.kareem.datalayer.model.common.UserModel
import io.reactivex.Observable
import io.reactivex.Single
import okhttp3.MultipartBody
import postGateWay
import retrofit2.Response

class PostRepositoryImplementer(
    private val postApi: PostsApi = postGateWay,
    private val preferences: PreferencesGateway = preferencesGateway
) : PostRepository
{

    override fun loadToken(): Single<String> {
        return preferences.load(prefKeys.TOKEN,"")
    }

    override fun hasSavedToken(): Single<Boolean> {
        return preferences.isSaved(prefKeys.TOKEN)
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

    override fun like(comment_id: String): Observable<Response<BaseResponse>> {
        return postApi.like(comment_id,loadToken().blockingGet())
    }

    override fun reply_like(comment_id: String): Observable<Response<BaseResponse>> {
        return postApi.like_reply(comment_id,loadToken().blockingGet())
    }

    override fun addComment(
        post_id: String,
        comment: String,
        image: MultipartBody.Part?
    ): Observable<Response<PostCommentModel>> {
        if (image==null) {
            return postApi.addComment(post_id, comment,loadToken().blockingGet())
        }
        else{
            return postApi.addComment(post_id, comment,image,loadToken().blockingGet())
        }
    }

    override fun getComments(post_id: String): Observable<Response<PostCommentModel>> {
        return postApi.comments(post_id,loadToken().blockingGet())
    }

    override fun commentReplies(comment_id: String): Observable<Response<PostCommentModel>> {
        return postApi.commentReplies(comment_id,loadToken().blockingGet())
    }

    override fun addReply(
        comment_id: String,
        reply: String,
        image: MultipartBody.Part?
    ): Observable<Response<PostCommentModel>> {
        if (image==null){
            return postApi.reply(comment_id, reply,loadToken().blockingGet())
        }
        else{
            return postApi.reply(comment_id, reply,image,loadToken().blockingGet())
        }

    }

    override fun post(post_id: String): Observable<Response<PostModel>> {
        return postApi.showPost(post_id,loadToken().blockingGet())
    }

    override fun addToCart(
        post_id: String,
        choose_call: Boolean?,
        choose_video: Boolean?
    ): Observable<Response<AddCartModel>> {
        return postApi.addTocart(post_id, choose_call, choose_video,loadToken().blockingGet())
    }

    override fun bookMarked(post_id: String): Observable<Response<AddCartModel>> {
        return postApi.bookmarkPost(post_id,loadToken().blockingGet())
    }

    override fun addPost(
        catId:String,
        content: String,
        price: String,
        call_price: String,
        video_price: String,
        deadline: String,
        images: List<MultipartBody.Part>?
    ): Observable<Response<BaseResponse>> {
        if (images.isNullOrEmpty()) {
            return postApi.addPost(
                catId,
                content, price, call_price, video_price, deadline,
                loadToken().blockingGet()
            )
        }
        else {
            return postApi.addPost(
                catId,
                content, price, call_price, video_price, deadline, images,
                loadToken().blockingGet()
            )
        }
    }


}