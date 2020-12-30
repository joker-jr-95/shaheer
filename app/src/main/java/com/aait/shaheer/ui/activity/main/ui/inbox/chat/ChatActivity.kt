package com.aait.shaheer.ui.activity.main.ui.inbox.chat

import android.app.Activity
import android.content.Intent
import android.util.Log
import android.view.View
import androidx.recyclerview.widget.LinearLayoutManager
import com.aait.shaheer.R
import com.aait.shaheer.data_layer.model.chat_model.Data
import com.aait.shaheer.data_layer.model.chat_model.Message
import com.aait.shaheer.domain.repository.chat_repository.inboxRepository
import com.fxn.pix.Options
import com.fxn.pix.Pix
import com.aait.shaheer.domain.core.Constant
import com.google.gson.Gson
import com.kareem.domain.core.Util
import com.kareem.domain.core.subscribeWithLoading
import com.kareem.domain.core.toasty
import com.kareem.presetntation.base.ParentActivity
import com.kareem.presetntation.helper.SocketConnection
import kotlinx.android.synthetic.main.activity_chat.*
import kotlinx.android.synthetic.main.item_bottom_chat.*
import kotlinx.android.synthetic.main.toolbar_chat.*
import okhttp3.MultipartBody
import org.jetbrains.anko.sdk27.coroutines.onClick
import org.json.JSONObject

class ChatActivity : ParentActivity() {
    private var mData: Data? = null
    private var myId: Int? = null
    private var conv_id: String? = "0"
    override val layoutResource: Int
        get() = R.layout.activity_chat

    private val mAdapter = ChatAdapter()

    var mFile:MultipartBody.Part? =null


    private  var allMsgs: MutableList<Message> = mutableListOf()


    override fun onStart() {
        super.onStart()
        addSocketConnection()
    }

    override fun onStop() {
        SocketConnection.onClose()
        super.onStop()

    }

    override fun init() {
        conv_id=intent.getStringExtra("id")
        setRec()
        sendRequest()
        connectSocket()
        setActions()
    }

    private fun setActions() {
        call_toolbar.onClick {
            sendRequestSession()
        }
        iv_send.onClick {
            if (editTextMessage.text.isNotBlank()){
                val msg=Message(content = editTextMessage.text.toString(),sender = "you",user_id = myId,type = "text",date = "الآن")
                allMsgs.add(msg)
                mAdapter.swapData(allMsgs)
                recycler_msg.scrollToPosition(allMsgs.lastIndex)
                Log.e("msgs",allMsgs.toString())
                emitSocketMessage("text",editTextMessage.text.toString())
                editTextMessage.text.clear()
            }
        }
        iv_capture.onClick {
            Pix.start(this@ChatActivity, Options.init().setExcludeVideos(false).setRequestCode(100))
        }

    }

    private fun sendRequestSession() {
        inboxRepository.getCreds(conv_id.toString()).subscribeWithLoading({showProgressDialog()},{
            hideProgressDialog()
            setSocket()
            startActivity(Intent(applicationContext,AudioActivity::class.java).apply {
                putExtra(AudioActivity.Tag,"publisher")
                putExtra(AudioActivity.token,it.data?.opentok_token)
                putExtra(AudioActivity.session,it.data?.session_token)
                putExtra("name",mData!!.seconduser!!.name)
                putExtra("avatar",mData!!.seconduser!!.avatar)
                putExtra("recv_id",mData!!.seconduser!!.id.toString())
                putExtra("conv_id",conv_id)
                putExtra(AudioActivity.key,it.data?.api_key)
            })
        },{},{})
    }

    private fun setSocket() {
        val jsonObject = JSONObject()

        jsonObject.put("conversation_id", Gson().toJson(conv_id!!.toInt()))
        jsonObject.put("sender_id", Gson().toJson(inboxRepository.loadUser()?.id))
        jsonObject.put("name", Gson().toJson(inboxRepository.loadUser()?.name))
        jsonObject.put("avatar", inboxRepository.loadUser()?.avatar)
        jsonObject.put("receiver_id", Gson().toJson(mData!!.seconduser!!.id))
        SocketConnection.addEvent("newCall",jsonObject)
    }

    private fun setRec() {
        recycler_msg.layoutManager=LinearLayoutManager(this)
        recycler_msg.adapter=mAdapter
        recycler_msg.setHasFixedSize(true)
        recycler_msg.setItemViewCacheSize(20)
        recycler_msg.isDrawingCacheEnabled = true
        recycler_msg.drawingCacheQuality = View.DRAWING_CACHE_QUALITY_HIGH

    }

    private fun addSocketConnection() {
        myId=inboxRepository.loadUser()?.id
        val jsonObject = JSONObject()
        jsonObject.put("conversation", conv_id)
        jsonObject.put("client", myId.toString())
        Log.e("addUser","added")
        SocketConnection.addEvent("adduser",jsonObject)
    }

    private fun emitSocketMessage(type:String,msg:String?=null,file:String?=null) {
        Log.e("socket","send")
        val jsonObject = JSONObject()
        jsonObject.put("sender_id", myId.toString())
        jsonObject.put("receiver_id", mData?.seconduser!!.id.toString())
        jsonObject.put("conversation_id", conv_id)
        jsonObject.put("time_zone", inboxRepository.loadUser()?.time_zone)
        jsonObject.put("content", msg ?: file)
        jsonObject.put("type", type)
        Log.e("addUser","added")
        Log.e("msgs",jsonObject.toString())
        SocketConnection.addEvent("sendmessage",jsonObject)
    }

    private fun connectSocket() {
        SocketConnection.mSocket.let {
            it!!.connect()
                    .on("message") {data->
                        if (SocketConnection.mSocket!!.connected()) {
                            Log.e("sig",data.toString())
                            val json = data[0]
                            val jsonData=json as JSONObject
                            Log.e("sig",jsonData.toString())
                             conv_id=jsonData["conversation_id"] as String
                            val sender_id=jsonData["sender_id"] as String
                            var content=jsonData["content"] as String
                            val type=jsonData["type"] as String
                            if (type!="text"){
                                if (!content.startsWith("http")){
                                    content= Constant.MEDIA_BASE+content
                                }
                            }
                            val msg=Message(mData?.seconduser!!.avatar,content,"now","true","seconduser",type,sender_id.toInt(),mData?.seconduser!!.name)
                            allMsgs.add(msg)
                            runOnUiThread {
                                Runnable {
                                }.run{
                                    mAdapter.swapData(allMsgs)
                                    recycler_msg.scrollToPosition(allMsgs.lastIndex)

                                }
                            }

                        }

                    }
        }
    }


    private fun sendRequest() {
        inboxRepository.chat(conv_id!!).subscribeWithLoading({showProgressDialog()},{
            mData=it.data
            it.data?.messages?.let { it1 ->
            handleMessages(
                it1
            )
        } },{handleError(it.message)},{})
    }

    private fun handleMessages(messages: List<Message>) {

        this.allMsgs= messages as MutableList<Message>
        mAdapter.swapData(allMsgs)
        hideProgressDialog()
        recycler_msg.scrollToPosition(allMsgs.lastIndex)

    }

    private fun handleError(message: String?) {
        hideProgressDialog()
        message?.let { toasty(it,2) }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (resultCode == Activity.RESULT_OK && requestCode == 100) {
            val returnValue =
                data!!.getStringArrayListExtra(Pix.IMAGE_RESULTS)
            //video
            if (returnValue[0].endsWith(".mp4")){

              //  video_view.setVideoURI(Uri.parse(returnValue[0]))
                //video_view.seekTo(10)
                Util.convertVideoToFilePix("file",returnValue[0])?.let {
                    mFile=it
                     }
                sendReuestUpload(mFile,"video")
            }
            //images
            else{
                Util.convertImgToFilePix("file",returnValue[0])?.let {
                    mFile=it

                }
                sendReuestUpload(mFile,"image")

            }


        }
    }


    private fun sendReuestUpload(part: MultipartBody.Part?,type: String) {
        if (type=="video"){
            inboxRepository.uploadFile(part!!).subscribeWithLoading({showProgressDialog()},{handlePart(it.data,type)},{handleError(it.message)},{})
        }
        else{
             inboxRepository.uploadFile(part!!).subscribeWithLoading({},{handlePart(it.data,type)},{handleError(it.message)},{})
            }
    }

    private fun handlePart(
        data: com.aait.shaheer.data_layer.model.upload_model.Data,
        type: String
    ) {
        hideProgressDialog()
        var msg=Message(content = data.url,type = type,user_id = myId,date = "now",sender = "you")
        allMsgs.add(msg)
        mAdapter.swapData(allMsgs)
        recycler_msg.scrollToPosition(allMsgs.lastIndex)
        emitSocketMessage(type = type,file = data.name)
    }

}