package com.aait.shaheer.ui.activity.main.ui.add_post

import android.annotation.SuppressLint
import android.app.Activity
import android.content.Intent
import android.net.Uri
import android.view.View
import android.widget.ArrayAdapter
import androidx.appcompat.widget.AppCompatAutoCompleteTextView
import androidx.recyclerview.widget.LinearLayoutManager
import com.aait.shaheer.R
import com.aait.shaheer.data_layer.model.categories_model.Data
import com.aait.shaheer.data_layer.model.common.ImageModel
import com.aait.shaheer.domain.repository.post_repository.postRepository
import com.aait.shaheer.domain.repository.profile_repository.profileRepository
import com.fxn.pix.Options
import com.fxn.pix.Pix
import com.kareem.domain.core.Util
import com.kareem.domain.core.subscribeWithLoading
import com.kareem.domain.core.toasty
import com.kareem.presetntation.base.ParentActivity
import com.kareem.presetntation.helper.setMyAdapter
import io.reactivex.Observable
import kotlinx.android.synthetic.main.activity_add_post.*
import okhttp3.MultipartBody
import org.jetbrains.anko.sdk27.coroutines.onClick

class AddPostActivity : ParentActivity() {
    private lateinit var mAdapter: ImageAdapter
    private  var cat_id: String=""
    override val layoutResource: Int
        get() = R.layout.activity_add_post

    var mImgs:MutableList<MultipartBody.Part> = mutableListOf()
    var mImgsVid:MutableList<MultipartBody.Part> = mutableListOf()



    override fun init() {
        setTitleToolbar(getString(R.string.add_post))
        setRec()
        setActions()
        sendRequestCats()
    }

    private fun sendRequestCats() {
        profileRepository.getCategories().subscribeWithLoading({showProgressDialog()}
            ,{handleCats(it.data)},{handleError(it.message)},{})
    }

    private fun handleError(message: String?) {
        hideProgressDialog()
        message?.let { toasty(it,2) }
    }

    private fun handleCats(data: List<Data>?) {
        hideProgressDialog()

        setSpinner(data?.map { it.name } as List<String>,etOccupations){ name->
            cat_id= data.filter { it.name==name }[0].id.toString()
        }

    }

    private fun setSpinner(data:List<String>, etLay: AppCompatAutoCompleteTextView, callSomeThing:(key:String)->Unit){
        val keysAdapter= ArrayAdapter(this, android.R.layout.simple_list_item_1,data)
        etLay.setMyAdapter(data,keysAdapter,true){
            callSomeThing(it)
        }

    }

    private fun setRec() {
         mAdapter=ImageAdapter{pos, item ->
             mImgs.removeAt(pos)
             mAdapter.removeItem(pos)

         }
        rec_imgs.layoutManager=LinearLayoutManager(applicationContext,LinearLayoutManager.HORIZONTAL,false)
        rec_imgs.adapter=mAdapter
    }

    private fun setActions() {
        add_btn.onClick {
            if (Util.checkError(etDesc,applicationContext,getString(R.string.error_content)) &&
                Util.checkError(etOccupations,applicationContext,getString(R.string.error_category)) &&
                Util.checkError(etPrice,applicationContext,getString(R.string.empty_field)) &&
                Util.checkError(etDeadLine,applicationContext,getString(R.string.empty_field)) &&
                Util.checkError(etCall,applicationContext,getString(R.string.empty_field)) &&
                Util.checkError(etVideo,applicationContext,getString(R.string.empty_field))
                    ) {
                if (mImgs.isNullOrEmpty() || mImgsVid.isNullOrEmpty()) {
                    if (mImgs.isNullOrEmpty()) {
                        //video
                        sendRequestAdd(mImgsVid)

                    } else {
                        //images
                       sendRequestAdd(mImgs)

                    }
                }
                else{

                    sendRequestAdd()
                }
            }
        }
        iv_add_img.onClick {
            Pix.start(this@AddPostActivity,Options.init().setExcludeVideos(false).setRequestCode(100))



        }


    }

    private fun sendRequestAdd(imgs:List<MultipartBody.Part>?=null) {

        postRepository.addPost(cat_id,etDesc.text.toString(),etPrice.text.toString()
        ,etCall.text.toString(),etVideo.text.toString(),etDeadLine.text.toString(),
            imgs
            ).subscribeWithLoading({showProgressDialog()},{handleResp(it.msg)},{handleError(it.message)},{})
    }

    private fun handleResp(msg: String?) {
        hideProgressDialog()
        toasty(msg!!)
        onBackPressed()
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (resultCode == Activity.RESULT_OK && requestCode == 100) {
            val returnValue =
                data!!.getStringArrayListExtra(Pix.IMAGE_RESULTS)
            //imgs
            if (returnValue[0].endsWith(".png")||returnValue[0].endsWith(".jpg")){
                mImgsVid.clear()
                rec_imgs.visibility= View.VISIBLE
                vid_lay.visibility=View.GONE
                for (imagPath in returnValue){
                    Util.convertImgToFilePix("images[]",imagPath)?.let { mImgs.add(it) }
                    mAdapter.addImg(ImageModel(src = imagPath))
                }
            }
            //vids
            else{
                rec_imgs.visibility= View.GONE
                vid_lay.visibility=View.VISIBLE
                video_view.setVideoURI(Uri.parse(returnValue[0]))
                video_view.seekTo(10)
                mImgsVid.clear()
                mImgs.clear()
                Util.convertVideoToFilePix("images[]",returnValue[0])?.let { mImgsVid.add(it) }
            }



        }
    }

}