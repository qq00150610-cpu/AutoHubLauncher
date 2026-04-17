package com.autohub.launcher.domain.model

data class AppInfo(
    val packageName: String,
    val name: String,
    val icon: Int = 0, // Resource ID for icon
    val versionName: String? = null,
    val versionCode: Long = 0,
    val isSystemApp: Boolean = false,
    val firstInstallTime: Long = 0,
    val lastUpdateTime: Long = 0,
    val size: Long = 0
)
