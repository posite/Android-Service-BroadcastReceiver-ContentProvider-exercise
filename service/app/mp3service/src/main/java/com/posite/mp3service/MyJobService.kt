package com.posite.mp3service

import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.job.JobParameters
import android.app.job.JobService
import android.util.Log

class MyJobService : JobService() {
    override fun onStartJob(params: JobParameters?): Boolean {
        Log.i("잡", "잡 서비스 시작")
        val manager = getSystemService(NOTIFICATION_SERVICE) as NotificationManager
        val channel =
            NotificationChannel("oneId", "oneName", NotificationManager.IMPORTANCE_DEFAULT)
        channel.description = "oneDesc"
        manager.createNotificationChannel(channel)
        Notification.Builder(this, "oneId")
            .run {
                setSmallIcon(android.R.drawable.ic_notification_overlay)
                setContentTitle("JobScheduler Title")
                setContentText("Content Message")
                setAutoCancel(true)
                manager.notify(1, build())
            }
        return false
    }

    override fun onStopJob(params: JobParameters?): Boolean {
        return true
    }
}