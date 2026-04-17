package com.autohub.launcher.service

import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.app.Service
import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.media.session.MediaController
import android.media.session.MediaSessionManager
import android.os.Build
import android.os.IBinder
import androidx.core.app.NotificationCompat
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MusicControlService : Service() {

    private lateinit var mediaSessionManager: MediaSessionManager
    private var mediaController: MediaController? = null

    override fun onCreate() {
        super.onCreate()
        mediaSessionManager = getSystemService(Context.MEDIA_SESSION_SERVICE) as MediaSessionManager
        setupMediaController()
    }

    private fun setupMediaController() {
        try {
            val activeSessions = mediaSessionManager.getActiveSessions(
                ComponentName(this, this.javaClass)
            )
            if (activeSessions.isNotEmpty()) {
                mediaController = MediaController(this, activeSessions[0].sessionToken)
            }
        } catch (e: SecurityException) {
            // Notification listener permission not granted
        }
    }

    fun playPause() {
        try {
            mediaController?.let { controller ->
                val playbackState = controller.playbackState
                if (playbackState != null && playbackState.state == android.media.session.PlaybackState.STATE_PLAYING) {
                    controller.transportControls.pause()
                } else {
                    controller.transportControls.play()
                }
            }
        } catch (e: Exception) {
            // Handle playback control error
        }
    }

    fun next() {
        try {
            mediaController?.transportControls?.skipToNext()
        } catch (e: Exception) {
            // Handle playback control error
        }
    }

    fun previous() {
        try {
            mediaController?.transportControls?.skipToPrevious()
        } catch (e: Exception) {
            // Handle playback control error
        }
    }

    override fun onBind(intent: Intent?): IBinder? = null
}
