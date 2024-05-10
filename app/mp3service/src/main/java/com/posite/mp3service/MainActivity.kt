package com.posite.mp3service

import android.app.job.JobInfo
import android.app.job.JobScheduler
import android.content.ComponentName
import android.content.Intent
import android.content.ServiceConnection
import android.content.pm.PackageManager
import android.os.Bundle
import android.os.Handler
import android.os.IBinder
import android.os.Looper
import android.os.Message
import android.os.Messenger
import android.util.Log
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import com.posite.mp3service.databinding.ActivityMainBinding
import com.posite.outer.MyAidlInterface
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

class MainActivity : AppCompatActivity() {
    private val binding by lazy { ActivityMainBinding.inflate(layoutInflater) }
    private var connectionMode = ""
    private lateinit var messenger: Messenger
    private lateinit var replyMessenger: Messenger
    private var messengerJob: Job? = null

    private var aidlService: MyAidlInterface? = null
    private var aidlJob: Job? = null

    private val messengerConnection: ServiceConnection = object : ServiceConnection {
        override fun onServiceConnected(name: ComponentName?, service: IBinder?) {
            Log.d("messenger", "messenger")
            connectionMode = "messenger"
            messenger = Messenger(service)
            replyMessenger = Messenger(HandlerReplyMsg())
            val msg = Message()
            msg.replyTo = replyMessenger
            msg.what = 0
            messenger.send(msg)
        }

        override fun onServiceDisconnected(name: ComponentName?) {

        }
    }

    private val aidlConnection: ServiceConnection = object : ServiceConnection {
        override fun onServiceConnected(name: ComponentName, service: IBinder) {
            Log.d("aidl", "aidl")
            aidlService = MyAidlInterface.Stub.asInterface(service)
            aidlService!!.start()
            binding.musicProgress.max = aidlService!!.maxDuration
            startAidlJob()
            connectionMode = "aidl"
            changeViewEnable()
        }

        override fun onServiceDisconnected(name: ComponentName?) {
            aidlService = null

        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(binding.root)
        onCreateMessengerService()
        onCreateAidlService()
        Log.i("알림", "외부 if")
        if (ContextCompat.checkSelfPermission(
                this,
                "android.permission.POST_NOTIFICATIONS"
            ) == PackageManager.PERMISSION_GRANTED
        ) {
            onCreateJobScheduler()
        }

    }

    override fun onStop() {
        super.onStop()
        if (connectionMode == "messenger") {
            onStopMessengerService()
        } else if (connectionMode == "aidl") {
            onStopAidlService()
        }
        connectionMode = "none"
        changeViewEnable()
    }

    private fun changeViewEnable() {
        when (connectionMode) {
            "messenger" -> {
                binding.messengerStartBtn.isEnabled = false
                binding.aidlStartBtn.isEnabled = false
                binding.messengerPauseBtn.isEnabled = true
                binding.aidlPauseBtn.isEnabled = false
            }

            "aidl" -> {
                binding.messengerStartBtn.isEnabled = false
                binding.aidlStartBtn.isEnabled = false
                binding.messengerPauseBtn.isEnabled = false
                binding.aidlPauseBtn.isEnabled = true
            }

            "messenger_pause" -> {
                binding.messengerStartBtn.isEnabled = true
                binding.aidlStartBtn.isEnabled = false
                binding.messengerPauseBtn.isEnabled = false
                binding.aidlPauseBtn.isEnabled = false
            }

            "aidl_pause" -> {
                binding.messengerStartBtn.isEnabled = false
                binding.aidlStartBtn.isEnabled = true
                binding.messengerPauseBtn.isEnabled = false
                binding.aidlPauseBtn.isEnabled = false
            }

            else -> {
                binding.messengerStartBtn.isEnabled = true
                binding.aidlStartBtn.isEnabled = true
                binding.messengerPauseBtn.isEnabled = false
                binding.aidlPauseBtn.isEnabled = false
            }
        }
    }

    inner class HandlerReplyMsg : Handler(Looper.getMainLooper()) {
        override fun handleMessage(msg: Message) {
            super.handleMessage(msg)
            when (msg.what) {
                0, 2 -> {
                    val bundle = msg.obj as Bundle
                    val duration = bundle.getInt("duration")
                    duration.let {
                        when {
                            it > 0 -> {
                                binding.musicProgress.max = duration
                                val backgroundScope = CoroutineScope(Dispatchers.Default + Job())
                                messengerJob = backgroundScope.launch {
                                    while (binding.musicProgress.progress < duration) {
                                        delay(1000)
                                        binding.musicProgress.incrementProgressBy(1000)
                                    }
                                }
                                changeViewEnable()
                            }

                            else -> {
                                connectionMode = "none"
                                changeViewEnable()
                            }
                        }
                    }
                }

                1 -> {
                    connectionMode = ""
                    changeViewEnable()
                }
            }
        }
    }

    private fun onCreateMessengerService() {
        replyMessenger = Messenger(HandlerReplyMsg())
        binding.messengerStartBtn.setOnClickListener {
            if (connectionMode == "messenger_pause") {
                Log.d("messenger", "pause")
                val msg = Message()
                msg.what = 2
                messenger.send(msg)
                connectionMode = "messenger"
                changeViewEnable()
            }
            val intent = Intent("ACTION_SERVICE_Messenger")
            intent.setPackage("com.posite.outer")
            bindService(intent, messengerConnection, BIND_AUTO_CREATE)
        }

        binding.messengerPauseBtn.setOnClickListener {
            Log.d("messenger", "pause")
            val msg = Message()
            msg.what = 1
            messenger.send(msg)
            messengerJob?.cancel()
            connectionMode = "messenger_pause"
            changeViewEnable()
        }
    }

    private fun onStopMessengerService() {
        val msg = Message()
        msg.what = 3
        messenger.send(msg)
        unbindService(messengerConnection)
    }

    private fun onCreateAidlService() {
        binding.aidlStartBtn.setOnClickListener {
            if (connectionMode == "aidl_pause") {
                aidlService?.start()
                connectionMode = "aidl"
                startAidlJob()
                changeViewEnable()
            } else {
                val intent = Intent("ACTION_SERVICE_AIDL")
                intent.setPackage("com.posite.outer")
                bindService(intent, aidlConnection, BIND_AUTO_CREATE)
            }
        }

        binding.aidlPauseBtn.setOnClickListener {
            aidlService?.pause()
            aidlJob?.cancel()
            connectionMode = "aidl_pause"
            changeViewEnable()
        }
    }

    private fun onStopAidlService() {
        unbindService(aidlConnection)
    }

    private fun onCreateJobScheduler() {
        Log.i("onCreateJobScheduler", "onCreateJobScheduler")
        var jobScheduler: JobScheduler? = getSystemService(JOB_SCHEDULER_SERVICE) as JobScheduler
        val builder = JobInfo.Builder(1, ComponentName(this, MyJobService::class.java))
        builder.setRequiredNetworkType(JobInfo.NETWORK_TYPE_ANY)
        val jobInfo = builder.build()
        jobScheduler!!.schedule(jobInfo)
    }

    private fun startAidlJob() {
        val backgroundScope = CoroutineScope(Dispatchers.Default + Job())
        aidlJob = backgroundScope.launch {
            while (binding.musicProgress.progress < aidlService!!.maxDuration) {
                delay(1000)
                binding.musicProgress.incrementProgressBy(1000)
            }
        }
    }
}