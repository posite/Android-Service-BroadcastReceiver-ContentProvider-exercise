package com.posite.broadcastreceiverex.battery

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.os.BatteryManager
import android.os.Bundle
import android.util.Log
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import com.posite.broadcastreceiverex.R
import com.posite.broadcastreceiverex.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {
    private val binding by lazy { ActivityMainBinding.inflate(layoutInflater) }
    private var br: BroadcastReceiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context, intent: Intent) {
            Log.d("이벤트", "onReceive()")
            val action = intent.action
            if (action == Intent.ACTION_BATTERY_CHANGED) {
                Log.d("이벤트", "충전상태")
                val remain = intent.getIntExtra(BatteryManager.EXTRA_LEVEL, 0)
                when (remain) {
                    //10, 20, 30, 40과 비교
                    in 81..95 -> binding.batteryImage.setImageResource(R.drawable.battery_high)
                    in 61..80 -> binding.batteryImage.setImageResource(R.drawable.battery_high)
                    in 26..60 -> binding.batteryImage.setImageResource(R.drawable.battery_half)
                    in 6..25 -> binding.batteryImage.setImageResource(R.drawable.battery_low)
                    in 0..5 -> binding.batteryImage.setImageResource(R.drawable.battery_empty)
                    else -> binding.batteryImage.setImageResource(R.drawable.battery_full)
                }
                binding.remainBatterText.text = getString(R.string.remain_battery, remain)
            }
            val plug = intent.getIntExtra(BatteryManager.EXTRA_PLUGGED, 0)
            when (plug) {
                0 -> binding.chargingModeText.text = "전원 연결 안됨"
                BatteryManager.BATTERY_PLUGGED_AC -> binding.chargingModeText.text = "AC 전원 연결됨"
                BatteryManager.BATTERY_PLUGGED_USB -> binding.chargingModeText.text = "USB 전원 연결됨"
                BatteryManager.BATTERY_PLUGGED_WIRELESS -> binding.chargingModeText.text =
                    "무선 전원 연결됨"

                else -> binding.chargingModeText.text = "알 수 없는 전원 연결됨"
            }
        }


    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(binding.root)
        /*ViewCompat.setOnApplyWindowInsetsListener(binding.main) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }*/

    }

    override fun onResume() {
        super.onResume()
        val intentFilter = IntentFilter()
        intentFilter.addAction(Intent.ACTION_BATTERY_CHANGED)
        registerReceiver(br, intentFilter)
    }

    override fun onPause() {
        super.onPause()
        unregisterReceiver(br)
    }
}