package com.autohub.launcher.data.local

import android.content.Context
import android.content.pm.ApplicationInfo
import android.content.pm.PackageManager
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import javax.inject.Inject
import javax.inject.Singleton

data class AppDetails(
    val packageName: String,
    val appName: String,
    val versionName: String,
    val versionCode: Long,
    val icon: Any, // android.graphics.drawable.Drawable
    val isSystemApp: Boolean,
    val size: Long,
    val cacheSize: Long,
    val dataSize: Long,
    val installTime: Long,
    val updateTime: Long,
    val permissions: List<String>
)

@Singleton
class AppManager @Inject constructor(
    @ApplicationContext private val context: Context
) {

    private val packageManager = context.packageManager
    private val _allApps = MutableStateFlow<List<AppDetails>>(emptyList())
    val allApps: StateFlow<List<AppDetails>> = _allApps.asStateFlow()

    private val _selectedApps = MutableStateFlow<Set<String>>(emptySet())
    val selectedApps: StateFlow<Set<String>> = _selectedApps.asStateFlow()

    init {
        loadAllApps()
    }

    fun loadAllApps() {
        val packages = packageManager.getInstalledApplications(PackageManager.GET_META_DATA)
        val appsList = packages.map { appInfo ->
            try {
                val appDetails = getAppDetails(appInfo)
                appDetails
            } catch (e: Exception) {
                null
            }
        }.filterNotNull().sortedBy { it.appName }

        _allApps.value = appsList
    }

    private fun getAppDetails(appInfo: ApplicationInfo): AppDetails {
        val packageInfo = packageManager.getPackageInfo(appInfo.packageName, 0)

        // Calculate app size (simplified)
        val apkFile = appInfo.sourceDir
        val size = java.io.File(apkFile).length()

        // Get permissions
        val permissions = packageInfo.requestedPermissions?.toList() ?: emptyList()

        return AppDetails(
            packageName = appInfo.packageName,
            appName = appInfo.loadLabel(packageManager).toString(),
            versionName = packageInfo.versionName ?: "",
            versionCode = packageInfo.longVersionCode,
            icon = appInfo.loadIcon(packageManager),
            isSystemApp = (appInfo.flags and ApplicationInfo.FLAG_SYSTEM) != 0,
            size = size,
            cacheSize = 0L, // Simplified - would need more work to get actual cache size
            dataSize = 0L, // Simplified
            installTime = packageInfo.firstInstallTime,
            updateTime = packageInfo.lastUpdateTime,
            permissions = permissions
        )
    }

    fun uninstallApp(packageName: String): Boolean {
        return try {
            val intent = android.content.Intent(android.content.Intent.ACTION_DELETE).apply {
                data = android.net.Uri.parse("package:$packageName")
                flags = android.content.Intent.FLAG_ACTIVITY_NEW_TASK
            }
            context.startActivity(intent)
            true
        } catch (e: Exception) {
            false
        }
    }

    fun launchApp(packageName: String): Boolean {
        return try {
            val intent = packageManager.getLaunchIntentForPackage(packageName)
            intent?.let {
                context.startActivity(it)
                true
            } ?: false
        } catch (e: Exception) {
            false
        }
    }

    fun getAppInfo(packageName: String): AppDetails? {
        return _allApps.value.find { it.packageName == packageName }
    }

    fun getSystemApps(): List<AppDetails> {
        return _allApps.value.filter { it.isSystemApp }
    }

    fun getUserApps(): List<AppDetails> {
        return _allApps.value.filter { !it.isSystemApp }
    }

    fun searchApps(query: String): List<AppDetails> {
        if (query.isBlank()) return _allApps.value

        return _allApps.value.filter { app ->
            app.appName.contains(query, ignoreCase = true) ||
            app.packageName.contains(query, ignoreCase = true)
        }
    }

    fun toggleAppSelection(packageName: String) {
        val currentSelection = _selectedApps.value.toMutableSet()
        if (currentSelection.contains(packageName)) {
            currentSelection.remove(packageName)
        } else {
            currentSelection.add(packageName)
        }
        _selectedApps.value = currentSelection
    }

    fun clearSelection() {
        _selectedApps.value = emptySet()
    }

    fun clearAppCache(packageName: String): Boolean {
        return try {
            // Simplified - actual cache clearing requires system privileges
            true
        } catch (e: Exception) {
            false
        }
    }

    fun clearAppData(packageName: String): Boolean {
        return try {
            // Simplified - actual data clearing requires system privileges
            true
        } catch (e: Exception) {
            false
        }
    }

    fun formatSize(size: Long): String {
        if (size < 1024) return "$size B"
        val kb = size / 1024.0
        if (kb < 1024) return String.format("%.2f KB", kb)
        val mb = kb / 1024.0
        if (mb < 1024) return String.format("%.2f MB", mb)
        val gb = mb / 1024.0
        return String.format("%.2f GB", gb)
    }

    fun formatDate(timestamp: Long): String {
        val date = java.util.Date(timestamp)
        val format = java.text.SimpleDateFormat("yyyy-MM-dd HH:mm", java.util.Locale.getDefault())
        return format.format(date)
    }
}
