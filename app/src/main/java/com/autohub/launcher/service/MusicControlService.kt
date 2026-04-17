package com.autohub.launcher.service

import android.content.ComponentName
import android.content.Context
import android.media.session.MediaController
import android.media.session.MediaSessionManager
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject
import javax.inject.Singleton

/**
 * 音乐控制服务
 * 提供媒体播放控制功能
 */
@Singleton
class MusicControlService @Inject constructor(
    @ApplicationContext private val context: Context
) {
    private var mediaSessionManager: MediaSessionManager? = null
    private var mediaController: MediaController? = null

    init {
        try {
            mediaSessionManager = context.getSystemService(Context.MEDIA_SESSION_SERVICE) as MediaSessionManager
            setupMediaController()
        } catch (e: Exception) {
            // Handle initialization error
        }
    }

    private fun setupMediaController() {
        try {
            val activeSessions = mediaSessionManager?.getActiveSessions(
                ComponentName(context, MusicControlService::class.java)
            )
            if (!activeSessions.isNullOrEmpty()) {
                mediaController = MediaController(context, activeSessions[0].sessionToken)
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
}
