package com.codewithkael.webrtcscreenshare.service

import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.Service
import android.content.Intent
import android.os.Build
import android.os.IBinder
import androidx.core.app.NotificationCompat
import com.codewithkael.webrtcscreenshare.R
import com.codewithkael.webrtcscreenshare.repository.MainRepository
import dagger.hilt.android.AndroidEntryPoint
import org.webrtc.MediaStream
import org.webrtc.SurfaceViewRenderer
import javax.inject.Inject

@AndroidEntryPoint
class WebrtcService @Inject constructor() : Service() , MainRepository.Listener {


    companion object {
        var screenPermissionIntent : Intent ?= null
        var surfaceView:SurfaceViewRenderer?=null
        var listener: MainRepository.Listener?=null
    }

    @Inject lateinit var mainRepository: MainRepository

    private lateinit var notificationManager: NotificationManager
    private lateinit var username:String

    override fun onCreate() {
        super.onCreate()
        notificationManager = getSystemService(
            NotificationManager::class.java
        )
        mainRepository.listener = this
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        if (intent!=null){
            when(intent.action){
                "StartIntent"->{
                    this.username = intent.getStringExtra("username").toString()
                    mainRepository.init(username, surfaceView!!)
                    startServiceWithNotification()
                }
                "StopIntent"->{
                    stopMyService()
                }
                "EndCallIntent"->{
                    mainRepository.sendCallEndedToOtherPeer()
                    mainRepository.onDestroy()
                    stopMyService()
                }
                "AcceptCallIntent"->{
                    val target = intent.getStringExtra("target")
                    target?.let {
                        mainRepository.startCall(it)
                    }
                }
                "RequestConnectionIntent"->{
                    val target= intent.getStringExtra("target")
                    target?.let {
                        mainRepository.setPermissionIntentToWebrtcClient(screenPermissionIntent!!)
                        mainRepository.startScreenCapturing(surfaceView!!)
                        mainRepository.sendScreenShareConnection(it)
                    }
                }
            }
        }

        return START_STICKY
    }

    private fun stopMyService(){
        mainRepository.onDestroy()
        stopSelf()
        notificationManager.cancelAll()
    }

    private fun startServiceWithNotification(){
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O){
            val notificationChannel = NotificationChannel(
                "channel1","foreground",NotificationManager.IMPORTANCE_HIGH
            )
            notificationManager.createNotificationChannel(notificationChannel)
            val notification = NotificationCompat.Builder(this,"channel1")
                .setSmallIcon(R.mipmap.ic_launcher)

            startForeground(1,notification.build())
        }

    }

    override fun onConnectionRequestReceived(target: String) {
        listener?.onConnectionRequestReceived(target)
    }

    override fun onConnectionConnected() {
        listener?.onConnectionConnected()
    }

    override fun onCallEndReceived() {
        listener?.onCallEndReceived()
        stopMyService()
    }

    override fun onRemoteStreamAdded(stream: MediaStream) {
        listener?.onRemoteStreamAdded(stream)
    }

    override fun onBind(intent: Intent?): IBinder? {
        return null
    }
}