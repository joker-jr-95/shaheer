package com.kareem.presetntation.helper
import android.util.Log
import com.github.nkzawa.socketio.client.IO
import com.github.nkzawa.socketio.client.Socket
import org.json.JSONObject
import java.net.URISyntaxException

object SocketConnection {
     var mSocket: Socket? = null
    val SOCKET_URL = "https://shaheer.4hoste.com:4449"


    init {
        try {
            mSocket = IO.socket(SOCKET_URL)
        }
        catch (e: URISyntaxException){
            Log.e("error",e.message)
        }
    }

    fun onListen(eventName:String,onConnect:(isConnected:Boolean,it: Array<Any>)->Unit){

        mSocket.let {

            it!!.connect()
                .on(eventName) {data->
                    if (mSocket!!.connected()) {
                        onConnect(true,data)
                    }
                    else{
                        onConnect(false,data)
                    }
                }
        }
    }
    fun onClose(){
        mSocket?.close()
    }

    fun addEvent(eventName: String,jsonObject:JSONObject) {
        mSocket!!.emit(eventName,jsonObject)
    }
    fun cancelEvent(eventName: String) {
        mSocket!!.off(eventName)

    }

}