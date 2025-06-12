package com.pnu.aidbtdiary.helper

import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import androidx.core.app.NotificationCompat

object NotificationUtil {
    private const val CHANNEL_ID = "sync_channel"
    private const val CHANNEL_NAME = "동기화 알림"

    fun createChannel(context: Context) {
        val channel = NotificationChannel(
            CHANNEL_ID, CHANNEL_NAME, NotificationManager.IMPORTANCE_DEFAULT
        ).apply {
            description = "일기 동기화 상태를 알리는 채널입니다."
            enableVibration(true)
            enableLights(true)
        }
        val manager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        manager.createNotificationChannel(channel)
    }

    fun notifySyncComplete(context: Context) {
        val builder = NotificationCompat.Builder(context.applicationContext, CHANNEL_ID)
            .setSmallIcon(android.R.drawable.stat_notify_sync)
            .setContentTitle("동기화 완료")
            .setContentText("일기 동기화가 성공적으로 완료되었습니다.")
            .setAutoCancel(true)
            .setPriority(NotificationCompat.PRIORITY_DEFAULT)
        val manager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        manager.notify(1, builder.build())
    }

    fun notifySyncError(context: Context, errorMessage: String) {
        val builder = NotificationCompat.Builder(context.applicationContext, CHANNEL_ID)
            .setSmallIcon(android.R.drawable.stat_notify_error)
            .setContentTitle("동기화 오류")
            .setContentText("일기 동기화 중 오류가 발생했습니다: $errorMessage")
            .setAutoCancel(true)
            .setPriority(NotificationCompat.PRIORITY_DEFAULT)
        val manager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        manager.notify(2, builder.build())
    }
}

