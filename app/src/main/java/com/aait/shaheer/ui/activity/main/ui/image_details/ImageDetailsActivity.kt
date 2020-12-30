package com.aait.shaheer.ui.activity.main.ui.image_details

import android.annotation.SuppressLint
import android.util.Log
import com.aait.shaheer.R
import com.davemorrissey.labs.subscaleview.ImageSource
import com.kareem.domain.core.Util
import com.kareem.presetntation.base.ParentActivity
import io.reactivex.Observable
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import kotlinx.android.synthetic.main.activity_image_details.*

class ImageDetailsActivity : ParentActivity() {
    override val layoutResource: Int
        get() = R.layout.activity_image_details

    @SuppressLint("CheckResult")
    override fun init() {
        val image = intent.getStringExtra("img")
        Observable.just(image)
            .subscribeOn(Schedulers.io())
            .map {
                Log.e("image",it)
                Util.getBitmapFromURL(image)!!
            }
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe {
                iv_details.setImage(ImageSource.bitmap(it))
            }
    }


}