package com.posite.serviceex

import android.content.ComponentName
import android.content.Intent
import android.content.ServiceConnection
import android.os.Bundle
import android.os.IBinder
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import com.posite.serviceex.databinding.ActivityMusicBinding

class MainActivity : AppCompatActivity() {
    private lateinit var musicServiceIntent: Intent
    private val binding by lazy { ActivityMusicBinding.inflate(layoutInflater) }
    private var isPlaying = false
    private var isResume = false
    private var musicService: MusicService? = null
    private val serviceConn = object : ServiceConnection {
        override fun onServiceConnected(name: ComponentName?, service: IBinder?) {
            Log.d("MainActivity", "onServiceConnected")
            val binder = service as MusicService.MusicServiceBinder
            musicService = binder.getService()
            isPlaying = true
            Log.d("MainActivity", musicService!!.bindServiceFun())
        }

        override fun onServiceDisconnected(name: ComponentName?) {
            Log.d("MainActivity", "onServiceDisconnected")
            isPlaying = false
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)
        musicServiceIntent = Intent(this, MusicService::class.java)
        binding.musicBtn.setOnClickListener {
            if (isResume) {
                binding.musicBtn.setImageResource(R.drawable.play)
                //stopService(musicServiceIntent)
                unbindMusicService()
            } else {
                binding.musicBtn.setImageResource(R.drawable.pause)
                //startService(musicServiceIntent)
                bindMusicService()
            }
        }
        binding.bindFunBtn.setOnClickListener {
            callBindServiceFun()
        }
    }

    //서비스 바인딩
    //호출될 때 서비스 생성되어있으면 바인딩, 아니면 생성 후 바인딩 됨
    private fun bindMusicService() {
        isResume = true
        if (isPlaying) {
            musicService!!.resumeMusic()
        } else {
            bindService(musicServiceIntent, serviceConn, BIND_AUTO_CREATE)
        }

    }

    //서비스 종료
    private fun unbindMusicService() {
        if (isPlaying) {
            musicService!!.pauseMusic()
            isResume = false
        }
    }

    private fun callBindServiceFun() {
        if (isPlaying) {
            val result = musicService?.bindServiceFun()
            Log.d("binding service fun", result!!)
        }
    }

    override fun onDestroy() {
        stopService(musicServiceIntent)
        super.onDestroy()
    }
}