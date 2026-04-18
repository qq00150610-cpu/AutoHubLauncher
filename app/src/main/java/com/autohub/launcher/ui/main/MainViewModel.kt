package com.autohub.launcher.ui.main

import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.autohub.launcher.domain.model.AppInfo
import com.autohub.launcher.domain.model.WeatherInfo
import com.autohub.launcher.domain.usecase.GetInstalledAppsUseCase
import com.autohub.launcher.domain.usecase.GetWeatherUseCase
import com.autohub.launcher.domain.usecase.GetBackgroundAppsUseCase
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import javax.inject.Inject

@HiltViewModel
class MainViewModel @Inject constructor(
    @ApplicationContext private val context: Context,
    private val getInstalledAppsUseCase: GetInstalledAppsUseCase,
    private val getWeatherUseCase: GetWeatherUseCase,
    private val getBackgroundAppsUseCase: GetBackgroundAppsUseCase
) : ViewModel() {

    private val _uiState = MutableStateFlow(MainUiState())
    val uiState: StateFlow<MainUiState> = _uiState.asStateFlow()

    private val _currentTime = MutableStateFlow("")
    val currentTime: StateFlow<String> = _currentTime.asStateFlow()

    init {
        loadInstalledApps()
        loadWeather()
        updateTime()
    }

    private fun loadInstalledApps() {
        viewModelScope.launch {
            getInstalledAppsUseCase().collect { apps ->
                _uiState.value = _uiState.value.copy(
                    installedApps = apps.take(8),
                    allApps = apps
                )
            }
        }
    }

    private fun loadWeather() {
        viewModelScope.launch {
            getWeatherUseCase().collect { weather ->
                _uiState.value = _uiState.value.copy(
                    weather = weather
                )
            }
        }
    }

    private fun loadBackgroundApps() {
        viewModelScope.launch {
            getBackgroundAppsUseCase().collect { apps ->
                _uiState.value = _uiState.value.copy(
                    backgroundApps = apps
                )
            }
        }
    }

    private fun updateTime() {
        viewModelScope.launch {
            while (true) {
                val now = LocalDateTime.now()
                val formatter = DateTimeFormatter.ofPattern("HH:mm")
                _currentTime.value = now.format(formatter)
                kotlinx.coroutines.delay(1000)
            }
        }
    }

    fun onResume() {
        loadBackgroundApps()
    }

    fun onPause() {
        // App is going to background
    }

    fun onAppClicked(app: AppInfo) {
        // 启动应用
        try {
            val intent = context.packageManager.getLaunchIntentForPackage(app.packageName)
            if (intent != null) {
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                context.startActivity(intent)
            }
        } catch (e: Exception) {
            // 启动失败
        }
    }

    fun openNavigationApp() {
        // 尝试打开导航应用
        val navPackages = listOf(
            "com.baidu.BaiduMap",           // 百度地图
            "com.autonavi.minimap",         // 高德地图
            "com.tencent.map",              // 腾讯地图
            "com.google.android.apps.maps"  // Google Maps
        )
        
        for (pkg in navPackages) {
            try {
                val intent = context.packageManager.getLaunchIntentForPackage(pkg)
                if (intent != null) {
                    intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                    context.startActivity(intent)
                    return
                }
            } catch (e: Exception) {
                continue
            }
        }
        
        // 如果没有导航应用，打开系统地图
        try {
            val intent = Intent(Intent.ACTION_VIEW, Uri.parse("geo:0,0?q="))
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            context.startActivity(intent)
        } catch (e: Exception) {
            // 没有地图应用
        }
    }

    fun openMusicApp() {
        // 尝试打开音乐应用
        val musicPackages = listOf(
            "com.kugou.android",            // 酷狗音乐
            "com.netease.cloudmusic",       // 网易云音乐
            "com.tencent.qqmusic",          // QQ音乐
            "com.android.music"             // 系统音乐
        )
        
        for (pkg in musicPackages) {
            try {
                val intent = context.packageManager.getLaunchIntentForPackage(pkg)
                if (intent != null) {
                    intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                    context.startActivity(intent)
                    return
                }
            } catch (e: Exception) {
                continue
            }
        }
    }

    fun openVideoApp() {
        // 尝试打开视频应用
        val videoPackages = listOf(
            "com.qiyi.video",               // 爱奇艺
            "com.youku.phone",              // 优酷
            "com.tencent.qqlive",           // 腾讯视频
            "tv.danmaku.bili"               // 哔哩哔哩
        )
        
        for (pkg in videoPackages) {
            try {
                val intent = context.packageManager.getLaunchIntentForPackage(pkg)
                if (intent != null) {
                    intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                    context.startActivity(intent)
                    return
                }
            } catch (e: Exception) {
                continue
            }
        }
    }

    fun openSettings() {
        try {
            val intent = Intent(android.provider.Settings.ACTION_SETTINGS)
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            context.startActivity(intent)
        } catch (e: Exception) {
            // 打开设置失败
        }
    }

    fun onBottomNavTabSelected(tab: BottomNavTab) {
        _uiState.value = _uiState.value.copy(
            selectedTab = tab
        )
        
        when (tab) {
            BottomNavTab.Navigation -> openNavigationApp()
            BottomNavTab.Music -> openMusicApp()
            BottomNavTab.Video -> openVideoApp()
            BottomNavTab.Settings -> openSettings()
            BottomNavTab.Home -> {}
        }
    }
}

data class MainUiState(
    val installedApps: List<AppInfo> = emptyList(),
    val allApps: List<AppInfo> = emptyList(),
    val backgroundApps: List<AppInfo> = emptyList(),
    val weather: WeatherInfo? = null,
    val selectedTab: BottomNavTab = BottomNavTab.Home,
    val isLoading: Boolean = false,
    val error: String? = null
)

enum class BottomNavTab {
    Home, Navigation, Music, Video, Settings
}
