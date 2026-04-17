package com.autohub.launcher.service

import android.content.Context
import android.content.Intent
import android.net.Uri
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject
import javax.inject.Singleton

/**
 * 导航服务
 * 提供导航启动和状态管理功能
 */
@Singleton
class NavigationService @Inject constructor(
    @ApplicationContext private val context: Context
) {
    fun startNavigation(destination: String, latitude: Double? = null, longitude: Double? = null) {
        // Try to launch navigation apps
        val intent = try {
            // Try Amap (高德)
            if (latitude != null && longitude != null) {
                Intent(Intent.ACTION_VIEW, Uri.parse("androidamap://navi?sourceApplication=AutoHub&lat=$latitude&lon=$longitude"))
            } else {
                Intent(Intent.ACTION_VIEW, Uri.parse("androidamap://navi?sourceApplication=AutoHub&poiname=$destination"))
            }
        } catch (e: Exception) {
            // Fallback to generic geo intent
            Intent(Intent.ACTION_VIEW, Uri.parse("geo:0,0?q=$destination"))
        }
        
        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK
        context.startActivity(intent)
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
    val distance: Int,  // meters
    val eta: Int        // seconds
)
