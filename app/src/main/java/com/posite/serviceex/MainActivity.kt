package com.posite.serviceex

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.posite.serviceex.databinding.ActivityMusicBinding

class MainActivity : AppCompatActivity() {
    private lateinit var musicServiceIntent: Intent
    private val binding by lazy { ActivityMusicBinding.inflate(layoutInflater) }
    private var isPlaying = false
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)
        musicServiceIntent = Intent(this, MusicService::class.java)
        binding.musicBtn.setOnClickListener {
            if (isPlaying) {
                binding.musicBtn.setImageResource(R.drawable.play)
                stopService(musicServiceIntent)
            } else {
                binding.musicBtn.setImageResource(R.drawable.pause)
                startService(musicServiceIntent)
            }
            isPlaying = isPlaying.not()
        }
    }

    override fun onDestroy() {
        stopService(musicServiceIntent)
        super.onDestroy()
    }
}