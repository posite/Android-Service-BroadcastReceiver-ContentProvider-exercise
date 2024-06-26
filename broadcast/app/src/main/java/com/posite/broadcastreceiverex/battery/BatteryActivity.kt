package com.posite.broadcastreceiverex.battery

import android.content.Intent
import android.content.IntentFilter
import android.content.pm.PackageManager
import android.os.BatteryManager
import android.os.Build
import android.os.Bundle
import android.util.Log
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.posite.broadcastreceiverex.R
import com.posite.broadcastreceiverex.databinding.ActivityBatteryBinding

class BatteryActivity : AppCompatActivity() {
    private val binding by lazy { ActivityBatteryBinding.inflate(layoutInflater) }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(binding.root)
        if (ContextCompat.checkSelfPermission(
                this,
                android.Manifest.permission.POST_NOTIFICATIONS
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            Log.d("권한", "권한 없음")
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                ActivityCompat.requestPermissions(
                    this,
                    arrayOf(android.Manifest.permission.POST_NOTIFICATIONS),
                    1001
                )
            }
        }

        registerReceiver(null, IntentFilter(Intent.ACTION_BATTERY_CHANGED))?.apply {
            when (getIntExtra(BatteryManager.EXTRA_STATUS, -1)) {
                BatteryManager.BATTERY_STATUS_CHARGING -> {
                    when (getIntExtra(BatteryManager.EXTRA_PLUGGED, -1)) {

                        BatteryManager.BATTERY_PLUGGED_AC -> {
                            binding.chargingModeText.text = "AC 전원 연결됨"
                            binding.chargeModeImage.setImageResource(R.drawable.charge_ac)
                        }

                        BatteryManager.BATTERY_PLUGGED_USB -> {
                            binding.chargingModeText.text = "USB 전원 연결됨"
                            binding.chargeModeImage.setImageResource(R.drawable.charge_usb)
                        }

                        BatteryManager.BATTERY_PLUGGED_WIRELESS -> {
                            binding.chargingModeText.text = "무선 전원 연결됨"
                            binding.chargeModeImage.setImageResource(R.drawable.charge_wireless)
                        }

                        -1 -> {
                            binding.chargingModeText.text = "전원 연결 안됨"
                            binding.chargeModeImage.setImageResource(R.drawable.charge_ban)
                        }
                    }
                }
            }
            val level = getIntExtra(BatteryManager.EXTRA_LEVEL, -1)
            val scale = getIntExtra(BatteryManager.EXTRA_SCALE, -1)
            val batteryPct = level / scale.toFloat() * 100
            binding.batterText.text = getString(R.string.remain_battery_float, batteryPct)
        }
        binding.broadcastButton.setOnClickListener {
            val intent = Intent(this, ChargerReceiver::class.java)
            sendBroadcast(intent)
        }
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == 1001) {
            binding.broadcastButton.isEnabled = true
        }
    }
}