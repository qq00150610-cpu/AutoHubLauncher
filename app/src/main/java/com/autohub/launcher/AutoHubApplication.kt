package com.autohub.launcher

import android.app.Application
import android.app.NotificationChannel
import android.app.NotificationManager
import android.os.Build
import android.os.Process
import dagger.hilt.android.HiltAndroidApp
import java.lang.Thread.UncaughtExceptionHandler

@HiltAndroidApp
class AutoHubApplication : Application() {

    companion object {
        const val NOTIFICATION_CHANNEL_ID = "autohub_notification_channel"
        const val NOTIFICATION_CHANNEL_ID_MUSIC = "autohub_music_channel"
        const val NOTIFICATION_CHANNEL_ID_NAV = "autohub_nav_channel"
        const val NOTIFICATION_CHANNEL_ID_SERVICE = "autohub_service_channel"
    }

    override fun onCreate() {
        super.onCreate()
        
        // 设置全局异常处理器
        setupExceptionHandler()
        
        createNotificationChannels()
    }

    private fun setupExceptionHandler() {
        val defaultHandler = Thread.getDefaultUncaughtExceptionHandler()
        Thread.setDefaultUncaughtExceptionHandler { thread, throwable ->
            // 记录异常信息
            android.util.Log.e("AutoHub", "Uncaught exception", throwable)
            
            // 调用默认处理器
            defaultHandler?.uncaughtException(thread, throwable) ?: run {
                Process.killProcess(Process.myPid())
                System.exit(1)
            }
        }
    }

    private fun createNotificationChannels() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val notificationManager = getSystemService(NotificationManager::class.java)

            // General notification channel
            val channelGeneral = NotificationChannel(
                NOTIFICATION_CHANNEL_ID,
                "AutoHub Notifications",
                NotificationManager.IMPORTANCE_DEFAULT
            ).apply {
                description = "General notifications from AutoHub"
            }

            // Music notification channel
            val channelMusic = NotificationChannel(
                NOTIFICATION_CHANNEL_ID_MUSIC,
                "Music Control",
                NotificationManager.IMPORTANCE_LOW
            ).apply {
                description = "Music playback controls"
            }

            // Navigation notification channel
            val channelNav = NotificationChannel(
                NOTIFICATION_CHANNEL_ID_NAV,
                "Navigation",
                NotificationManager.IMPORTANCE_HIGH
            ).apply {
                description = "Navigation instructions"
            }

            // Service notification channel
            val channelService = NotificationChannel(
                NOTIFICATION_CHANNEL_ID_SERVICE,
                "Foreground Services",
                NotificationManager.IMPORTANCE_MIN
            ).apply {
                description = "Background services"
            }

            notificationManager.createNotificationChannel(channelGeneral)
            notificationManager.createNotificationChannel(channelMusic)
            notificationManager.createNotificationChannel(channelNav)
            notificationManager.createNotificationChannel(channelService)
        }
    }
}
