package com.posite.broadcastreceiverex.call

import android.content.pm.PackageManager
import android.icu.text.SimpleDateFormat
import android.os.Build
import android.os.Bundle
import android.provider.CallLog
import android.util.Log
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.LinearLayoutManager
import com.posite.broadcastreceiverex.databinding.ActivityCallHistoryBinding
import java.util.Date
import java.util.Locale

class CallHistoryActivity : AppCompatActivity() {
    private val binding by lazy { ActivityCallHistoryBinding.inflate(layoutInflater) }
    private val adapter by lazy { CallHistoryAdapter() }
    private val callHistoryList: ArrayList<CallHistory> = ArrayList()
    private val permissions: ArrayList<String> = ArrayList()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(binding.root)
        if (ContextCompat.checkSelfPermission(
                this,
                android.Manifest.permission.READ_CALL_LOG
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            Log.d("권한", "권한 없음")
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                permissions.add(android.Manifest.permission.READ_CALL_LOG)
            }
        } else {
            binding.callHistoryBtn.isEnabled = true
        }
        if (permissions.isNotEmpty()) {
            requestPermissions(permissions.toTypedArray(), 1001)
        } else {
            Log.d("권한", "권한 있음")
        }
        binding.callHistoryRv.adapter = adapter
        binding.callHistoryRv.layoutManager = LinearLayoutManager(this)
        binding.callHistoryBtn.setOnClickListener {
            findCallHistory()
        }
    }

    private fun findCallHistory() {
        var callSet = arrayOf(
            CallLog.Calls.DATE,
            CallLog.Calls.TYPE,
            CallLog.Calls.NUMBER,
            CallLog.Calls.DURATION
        )
        var cursor = contentResolver.query(
            CallLog.Calls.CONTENT_URI,
            callSet,
            null,
            null,
            null
        )
        if (cursor!!.count == 0) {
            return
        }
        cursor.moveToFirst()
        do {
            val callDate = cursor.getLong(0)
            val date = DATE_FORMATTER.format(Date(callDate))
            val callType = cursor.getInt(1)
            var type: String = ""
            when (callType) {
                CallLog.Calls.INCOMING_TYPE -> type = "수신"
                CallLog.Calls.OUTGOING_TYPE -> type = "발신"
                CallLog.Calls.MISSED_TYPE -> type = "부재중"
                CallLog.Calls.VOICEMAIL_TYPE -> type = "보이스메일"
                CallLog.Calls.REJECTED_TYPE -> type = "거부"
                CallLog.Calls.BLOCKED_TYPE -> type = "차단됨"
            }
            val number = cursor.getString(2)
            val duration = cursor.getString(3) + "초"
            callHistoryList.add(CallHistory(date, type, number, duration))
        } while (cursor.moveToNext())
        adapter.submitList(callHistoryList.toList())
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == 1001) {
            binding.callHistoryBtn.isEnabled = true
        }
    }

    companion object {
        private val DATE_FORMATTER = SimpleDateFormat("yyyy-MM-dd", Locale.KOREA)
    }
}