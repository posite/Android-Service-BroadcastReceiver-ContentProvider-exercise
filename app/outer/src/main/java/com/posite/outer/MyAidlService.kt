package com.posite.outer

import android.app.Service
import android.content.Intent
import android.media.MediaPlayer
import android.os.IBinder

class MyAidlService : Service() {
    lateinit var player: MediaPlayer
    override fun onCreate() {
        super.onCreate()
        player = MediaPlayer.create(this@MyAidlService, R.raw.cherryblossom)
    }

    override fun onDestroy() {
        super.onDestroy()
        player.release()
    }

    override fun onBind(intent: Intent): IBinder {
        return object : MyAidlInterface.Stub() {
            override fun getMaxDuration(): Int {
                return if (player.isPlaying) player.duration else 0
            }

            override fun start() {
                if (!player.isPlaying) {
                    player.start()
                }
            }

            override fun pause() {
                if (player.isPlaying) {
                    player.pause()
                }
            }

        }
    }
}