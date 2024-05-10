package com.posite.outer

import android.app.Service
import android.content.Context
import android.content.Intent
import android.media.MediaPlayer
import android.os.Bundle
import android.os.Handler
import android.os.IBinder
import android.os.Looper
import android.os.Message
import android.os.Messenger

class MessengerService : Service() {
    lateinit var messenger: Messenger
    lateinit var replyMessenger: Messenger
    lateinit var player: MediaPlayer

    inner class IncomingHandler(
        context: Context,
        private val applicationContext: Context = context.applicationContext
    ) : Handler(Looper.getMainLooper()) {
        override fun handleMessage(msg: Message) {
            when (msg.what) {
                0 -> {
                    replyMessenger = msg.replyTo
                    if (!player.isPlaying) {
                        try {
                            val replyMsg = Message()
                            replyMsg.what = 0
                            val replyBundle = Bundle()
                            replyBundle.putInt("duration", player.duration)
                            replyMsg.obj = replyBundle
                            replyMessenger.send(replyMsg)

                            player.start()
                        } catch (e: Exception) {
                            e.printStackTrace()
                        }
                    }
                }

                1 -> {
                    if (player.isPlaying) {
                        player.pause()
                    }
                }

                2 -> {
                    try {
                        val replyMsg = Message()
                        replyMsg.what = 0
                        val replyBundle = Bundle()
                        replyBundle.putInt("duration", player.duration)
                        replyMsg.obj = replyBundle
                        replyMessenger.send(replyMsg)

                        player.start()
                    } catch (e: Exception) {
                        e.printStackTrace()
                    }
                }

                else -> super.handleMessage(msg)
            }
        }
    }

    override fun onCreate() {
        super.onCreate()
        player = MediaPlayer.create(this@MessengerService, R.raw.cherryblossom)
    }

    override fun onDestroy() {
        super.onDestroy()
        player.release()
    }

    override fun onBind(intent: Intent?): IBinder? {
        messenger = Messenger(IncomingHandler(this))
        return messenger.binder
    }

}