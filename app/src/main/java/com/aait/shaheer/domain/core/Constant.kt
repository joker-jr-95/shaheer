package com.aait.shaheer.domain.core

import android.content.pm.PackageManager
import com.aait.shaheer.BuildConfig


object Constant {
    val MEDIA_BASE = "https://shaheer.4hoste.com/chatuploads/"
    val POSTS_BASE = "https://shaheer.4hoste.com/post/"
    val APP_BASE = "https://shaheer.4hoste.com/"
    val PAYMENT_URL="http://taxi.4hoste.com/payment"
    val HELP_URL   ="http://taxi.4hoste.com/payment"
    val APP_URL   ="https://play.google.com/store/apps/details?id=${BuildConfig.APPLICATION_ID}"
    val ZOOM = 14f
    //location
    const val SUCCESS_RESULT = 0
    const val FAILURE_RESULT = 1
    const val PACKAGE_NAME = "com.google.android.gms.location.sample.locationaddress"
    const val RECEIVER = "$PACKAGE_NAME.RECEIVER"
    const val RESULT_DATA_KEY = "$PACKAGE_NAME.RESULT_DATA_KEY"
    const val LOCATION_DATA_EXTRA = "$PACKAGE_NAME.LOCATION_DATA_EXTRA"

    const  val CAMERA_MAX_FILE_SIZE_BYTE = 2 * 1024 * 1024
    const  val REQUEST_CODE_TAKE_PHOTO = 101
    const  val REQUEST_CODE_PICK_PHOTO = 102
    object UserType {
        val INVESTOR = 1
        val NON_INVESTOR = 2

    }

    object PermissionCode {
        val STORAGE = 1
        val CAMERA = 8
        val MY_PERMISSIONS_REQUEST_READ_CONTACTS = 105

    }


    sealed class Chat{
        val LINK="link"
        val MESSAGE="text"
        val VIDEO="video"
    }
    object RequestCode {
        var GETLOCATION = 500
        val PHOTO_CHOOSE = 3
        val GPSEnabling = 300
        val Call = 100
        val Take_PICTURE = 9


    }

}
