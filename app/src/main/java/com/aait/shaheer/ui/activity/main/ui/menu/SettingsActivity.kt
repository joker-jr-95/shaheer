package com.aait.shaheer.ui.activity.main.ui.menu

import android.content.ComponentName
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import com.aait.shaheer.R
import com.aait.shaheer.domain.repository.menu_repository.menuRepository
import com.aait.shaheer.ui.activity.SplashActivity
import com.afollestad.materialdialogs.MaterialDialog
import com.afollestad.materialdialogs.list.listItemsSingleChoice
import com.kareem.domain.core.Util
import com.kareem.domain.core.subscribeWithLoading
import com.kareem.domain.core.toasty
import com.kareem.presetntation.base.ParentActivity
import kotlinx.android.synthetic.main.activity_settings.*
import org.jetbrains.anko.sdk27.coroutines.onCheckedChange
import org.jetbrains.anko.sdk27.coroutines.onClick

class SettingsActivity : ParentActivity() {
    private var isNotify: Boolean=true
    override val layoutResource: Int
        get() = R.layout.activity_settings

    override fun init() {
        setTitleToolbar(getString(R.string.settings))
        setActions()
        sendRequestDevice()
    }

    private fun setActions() {

        lang_lay.onClick {
            showLang(arrayListOf("English","العربية"))
        }
        switch_.onCheckedChange { buttonView, isChecked ->
            sendRequest(isChecked)
        }
        pass_lay.onClick {
            startActivity(Intent(applicationContext,ChangePassActivity::class.java))
        }

        block_lay.onClick {
            startActivity(Intent(applicationContext,BlockListActivity::class.java))
        }
    }

    private fun showLang(langs:ArrayList<String>) {

        val dialog = MaterialDialog(this)
        dialog.title(R.string.app_language)

        dialog.positiveButton(res = R.string.confirm)

        dialog.show {
            cornerRadius(16f)
            theme.applyStyle(R.style.AppTheme_Custom,true)
            listItemsSingleChoice(res = R.string.language_settings, items = langs)
            {dialog, indices, item ->

                if (item.toString()=="English"){
                    updateLang("en")

                }
                else{
                    updateLang("ar")
                }

            }


        }


    }
    private fun updateLang(lang: String) {
        menuRepository.lang=lang
        Util.changeLang(lang, applicationContext)
        val intent =  Intent(applicationContext,SplashActivity::class.java);
        startActivity(intent)
        finishAffinity()
    }

    private fun sendRequestDevice() {
        menuRepository.deviceData().subscribeWithLoading({
            showProgressDialog()
        },{
            if (it.value=="1"){
                isNotify=it.data.ordersNotify!!.toBoolean()

                switch_.isChecked=isNotify

            }
            else{
              //  toasty(it.msg!!,2)
            }
        },{
            hideProgressDialog()
          //  toasty(getString(R.string.error_connection),2)
            Log.e("error",it.message!!)
        },{
            hideProgressDialog()

        })
    }

    private fun sendRequest(checked: Boolean) {
        menuRepository.updateNotif(isNotify=checked)
            .subscribeWithLoading({
                //  showProgressDialog()
            },{
                if (it.value=="1"){
                    //toasty(it.msg!!)
                }
                else{
                  //  toasty(it.msg!!,2)
                }
            },
                {
                    hideProgressDialog()
                    toasty(getString(R.string.error_connection),2)
                    // Log.e("error",it.message!!)
                },{
                    hideProgressDialog()
                })

    }
}