package com.autohub.launcher.service

import android.app.Service
import android.content.Context
import android.content.Intent
import android.os.IBinder
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class NavigationService : Service() {

    override fun onBind(intent: Intent?): IBinder? {
        return null
    }

    fun startNavigation(destination: String) {
        // TODO: Implement navigation integration
        // Support for: 高德地图, 百度地图, 腾讯地图
        // Use Intent to launch navigation apps
    }

    fun getNavigationStatus(): NavigationStatus {
        // TODO: Get current navigation status
        return NavigationStatus(
            isActive = false,
            destination = "",
            distance = 0,
            eta = 0
        )
    }
}

data class NavigationStatus(
    val isActive: Boolean,
    val destination: String,
    val distance: Int, // in meters
    val eta: Int // in seconds
)
