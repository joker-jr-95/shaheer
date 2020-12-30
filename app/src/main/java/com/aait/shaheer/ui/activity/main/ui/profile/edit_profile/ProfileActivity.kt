package com.aait.shaheer.ui.activity.main.ui.profile.edit_profile

import android.app.Activity
import android.content.Intent
import android.graphics.BitmapFactory
import android.util.Log
import android.widget.ArrayAdapter
import androidx.appcompat.widget.AppCompatAutoCompleteTextView
import com.aait.shaheer.R
import com.aait.shaheer.data_layer.model.categories_model.Data
import com.aait.shaheer.data_layer.model.register_model.RegisterModel
import com.aait.shaheer.domain.repository.base_repository.baseRepository
import com.aait.shaheer.domain.repository.home_repository.homeRepository
import com.aait.shaheer.domain.repository.profile_repository.profileRepository
import com.fxn.pix.Options
import com.fxn.pix.Pix
import com.kareem.datalayer.model.common.UserModel
import com.kareem.domain.core.Util
import com.kareem.domain.core.subscribeWithLoading
import com.kareem.domain.core.toasty
import com.kareem.presetntation.base.ParentActivity
import com.kareem.presetntation.helper.setMyAdapter
import com.squareup.picasso.Picasso
import kotlinx.android.synthetic.main.activity_profile.*
import okhttp3.MultipartBody
import org.jetbrains.anko.sdk27.coroutines.onClick

class ProfileActivity : ParentActivity() {
    private var cat_id: String? = null
    private var mGender: String? = null
    private var mImgCover: MultipartBody.Part? = null
    private var mImgAvatar: MultipartBody.Part? = null
    private val REQ_CODE_AVATAR: Int=100
    private val REQ_CODE_COVER: Int = 200
    private  var mCategories: List<Data> = mutableListOf()
    private var mUser: UserModel? = null

    override val layoutResource: Int
        get() = R.layout.activity_profile

    override fun init() {
        mUser = profileRepository.loadUser()
        sendRequestCats()
        setActions()

    }

    private fun setActions() {
        iv_add_photo.onClick {
            Pix.start(this@ProfileActivity, Options.init().setRequestCode(REQ_CODE_AVATAR))
        }
        iv_add_cover.onClick {
            Pix.start(this@ProfileActivity, Options.init().setRequestCode(REQ_CODE_COVER))

        }

        save_btn.onClick {
            if (Util.checkError(etName,this@ProfileActivity,getString(R.string.invalid_name))
                && Util.checkEmail(etMail,applicationContext)
                /*&& Util.checkPhone(etPhone,input_lay,getString(R.string.invalid_phone))*/
            ){
                sendRequestEdit()
            }
        }



    }

    private fun sendRequestEdit() {
        profileRepository.editProfile("",etMail.text.toString(),etName.text.toString(),
            cat_id,mGender,etDesc.text.toString(),mImgAvatar,mImgCover
            ).subscribeWithLoading({showProgressDialog()},{handleDataProfile(it)},{ it.message?.let { it1 ->
            handleError(
                it1
            )
        } },{})
    }

    private fun handleDataProfile(data: RegisterModel) {
        hideProgressDialog()
        toasty(data.msg.toString())
        profileRepository.saveToken(data)
        profileRepository.saveUser(data)
        onBackPressed()
    }

    private fun sendRequestCats() {
        profileRepository.getCategories().subscribeWithLoading({showProgressDialog()}
            ,{ it.data?.let { it1 -> handleData(it1) } },{ it.message?.let { it1 -> handleError(it1) } },{})
    }

    private fun handleError(message: String) {
        hideProgressDialog()
        Log.e("error",message)
        toasty(message,2)
    }

    private fun handleData(data: List<Data>) {
        hideProgressDialog()
        mCategories=data
        setProfile()
    }

    private fun setProfile() {
        mUser?.avatar?.let { Picasso.get().load(it).into(iv_avatar)/*setImg(it,iv_avatar)*/ }
        mUser?.cover?.let {Picasso.get().load(it).into(iv_cover) /*setImg(it,iv_cover) */}

        etName.setText(mUser?.name)
        etMail.setText(mUser?.email)
        etOccupations.setText(mUser?.category)
        if (baseRepository.lang=="ar") {
            if (mUser?.gender == "male") {
                etGender.setText("ذكر")
            } else {
                etGender.setText("أنثي")
            }
        }
        else{
            etGender.setText(mUser?.gender)
        }
        mGender=mUser?.gender
        cat_id=mUser?.category_id.toString()
        etPhone.setText(mUser?.phone)
        etDesc.setText(mUser?.bio)
        setSpinner(mutableListOf(getString(R.string.male), getString(R.string.female)),etGender
        ) {
            mGender=it
        }
        setSpinner(mCategories.map { it.name } as List<String>,etOccupations){name->
           cat_id= mCategories.filter { it.name==name }[0].id.toString()
        }

    }

    private fun setSpinner(data:List<String>, etLay: AppCompatAutoCompleteTextView, callSomeThing:(key:String)->Unit){
        val keysAdapter= ArrayAdapter(this, android.R.layout.simple_list_item_1,data)
        etLay.setMyAdapter(data,keysAdapter,true){
            callSomeThing(it)
        }

    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
            if (resultCode==Activity.RESULT_OK){
                if (requestCode==REQ_CODE_AVATAR){
                    val returnValue = data!!.getStringArrayListExtra(Pix.IMAGE_RESULTS)[0]
                    mImgAvatar= Util.convertImgToFilePix("avatar",returnValue)
                    iv_avatar.setImageBitmap(BitmapFactory.decodeFile(returnValue))

                }
                else if (requestCode==REQ_CODE_COVER){
                    val returnValue = data!!.getStringArrayListExtra(Pix.IMAGE_RESULTS)[0]
                    mImgCover= Util.convertImgToFilePix("cover",returnValue)
                    iv_cover.setImageBitmap(BitmapFactory.decodeFile(returnValue))

                }
            }
    }

}