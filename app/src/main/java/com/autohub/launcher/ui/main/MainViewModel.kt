package com.autohub.launcher.ui.main

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.autohub.launcher.domain.model.AppInfo
import com.autohub.launcher.domain.model.WeatherInfo
import com.autohub.launcher.domain.usecase.GetInstalledAppsUseCase
import com.autohub.launcher.domain.usecase.GetWeatherUseCase
import com.autohub.launcher.domain.usecase.GetBackgroundAppsUseCase
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
                    installedApps = apps.take(8), // Show first 8 apps as shortcuts
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
        // Launch app
    }

    fun onBottomNavTabSelected(tab: BottomNavTab) {
        _uiState.value = _uiState.value.copy(
            selectedTab = tab
        )
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
