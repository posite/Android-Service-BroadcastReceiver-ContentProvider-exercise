package com.posite.serviceex

import android.app.Service
import android.content.Intent
import android.media.MediaPlayer
import android.os.IBinder
import android.util.Log

class MusicService : Service() {
    override fun onBind(intent: Intent?): IBinder? = null
    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        Log.d("MusicService", "onStartCommand")
        player = MediaPlayer.create(this, R.raw.cherryblossom)
        player.isLooping = true
        player.start()
        return super.onStartCommand(intent, flags, startId)
    }

    override fun onCreate() {
        Log.d("MusicService", "onCreate")
        super.onCreate()
    }

    override fun onDestroy() {
        Log.d("MusicService", "onDestroy")
        player.stop()
        player.release()
        super.onDestroy()
    }

    companion object {
        private lateinit var player: MediaPlayer
    }
}