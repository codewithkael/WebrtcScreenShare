package com.codewithkael.webrtcscreenshare.socket

import android.util.Log
import com.codewithkael.webrtcscreenshare.utils.DataModel
import com.codewithkael.webrtcscreenshare.utils.DataModelType
import com.google.gson.Gson
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import org.java_websocket.client.WebSocketClient
import org.java_websocket.handshake.ServerHandshake
import java.net.URI
import javax.inject.Inject
import javax.inject.Singleton
import kotlin.Exception

@Singleton
class SocketClient @Inject constructor(
    private val gson:Gson
){
    private var username:String?=null
    companion object {
        private var webSocket:WebSocketClient?=null
    }

    var listener:Listener?=null
    fun init(username:String){
        this.username = username

        webSocket= object : WebSocketClient(URI("ws://10.0.2.2:3000")){
            override fun onOpen(handshakedata: ServerHandshake?) {
                sendMessageToSocket(
                    DataModel(
                        type = DataModelType.SignIn,
                        username = username,
                        null,
                        null
                    )
                )
            }

            override fun onMessage(message: String?) {
                val model = try {
                    gson.fromJson(message.toString(),DataModel::class.java)
                }catch (e:Exception){
                    null
                }
                Log.d("TAG", "onMessage: $model")
                model?.let {
                    listener?.onNewMessageReceived(it)
                }
            }

            override fun onClose(code: Int, reason: String?, remote: Boolean) {
                CoroutineScope(Dispatchers.IO).launch {
                    delay(5000)
                    init(username)
                }
            }

            override fun onError(ex: Exception?) {
            }

        }
        webSocket?.connect()
    }


    fun sendMessageToSocket(message:Any?){
        try {
            webSocket?.send(gson.toJson(message))
        }catch (e:Exception){
            e.printStackTrace()
        }
    }

    fun onDestroy(){
        webSocket?.close()
    }

    interface Listener {
        fun onNewMessageReceived(model:DataModel)
    }
}