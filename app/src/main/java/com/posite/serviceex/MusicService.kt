package com.posite.serviceex

import android.app.Service
import android.content.Intent
import android.media.MediaPlayer
import android.os.IBinder
import android.util.Log

class MusicService : Service() {
    private val binder = MusicServiceBinder()
    override fun onBind(intent: Intent?): IBinder? {
        player = MediaPlayer.create(this, R.raw.cherryblossom)
        player.isLooping = true
        player.start()
        return binder
    }

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

    inner class MusicServiceBinder : android.os.Binder() {
        fun getService(): MusicService {
            Log.d("MusicService", "getService")
            return this@MusicService
        }
    }

    fun bindServiceFun(): String {
        Log.d("MusicService", "bindServiceFun")
        return "MusicService bindServiceFun"
    }

    fun pauseMusic() {
        player.pause()
    }

    fun resumeMusic() {
        player.start()
    }

    companion object {
        private lateinit var player: MediaPlayer
    }
}