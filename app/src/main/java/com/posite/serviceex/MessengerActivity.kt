package com.posite.serviceex

import android.content.ComponentName
import android.content.Intent
import android.content.ServiceConnection
import android.os.Bundle
import android.os.IBinder
import android.os.Message
import android.os.Messenger
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import com.posite.serviceex.databinding.ActivityMessengerBinding
import com.posite.serviceex.service.MessengerService

class MessengerActivity : AppCompatActivity() {
    private val binding by lazy { ActivityMessengerBinding.inflate(layoutInflater) }
    private lateinit var messenger: Messenger
    private val messengerConnection = object : ServiceConnection {
        override fun onServiceConnected(name: ComponentName?, service: IBinder?) {
            Log.d("MessengerActivity", "onServiceConnected")
            messenger = Messenger(service)
        }

        override fun onServiceDisconnected(name: ComponentName?) {
            Log.d("MessengerActivity", "onServiceDisconnected")
        }
    }


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)

        //앱 내부
        val messengerServiceIntent = Intent(this, MessengerService::class.java)
        bindService(messengerServiceIntent, messengerConnection, BIND_AUTO_CREATE)

        val outerIntent = Intent("ACTION_OUTER_SERVICE")
        intent.setPackage("com.posite.serviceex")

        binding.sendMessageBtn.setOnClickListener {
            /*
            앱 내부 메신저 전달
            val msg = Message()
            msg.what = 1
            msg.obj = "MessengerService 1"
            messenger.send(msg)
            */

            // 앱 외부 메신저 데이터 bundle로 전달
            val bundle = Bundle()
            bundle.putInt("data", 100)
            val msg = Message()
            msg.what = 1
            msg.obj = bundle
            messenger.send(msg)
        }
    }
}