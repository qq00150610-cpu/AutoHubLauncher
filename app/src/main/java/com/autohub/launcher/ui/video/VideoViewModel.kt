package com.autohub.launcher.ui.video

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.autohub.launcher.domain.usecase.GetInstalledAppsUseCase
import com.autohub.launcher.service.CarControlService
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class VideoViewModel @Inject constructor(
    private val getInstalledAppsUseCase: GetInstalledAppsUseCase,
    private val carControlService: CarControlService
) : ViewModel() {

    private val _uiState = MutableStateFlow(VideoUiState())
    val uiState: StateFlow<VideoUiState> = _uiState.asStateFlow()

    init {
        loadVideoApps()
        monitorVehicleSpeed()
    }

    private fun loadVideoApps() {
        viewModelScope.launch {
            val allApps = getInstalledAppsUseCase()
            allApps.collect { apps ->
                val videoApps = apps.filter { app ->
                    app.packageName.contains("video") ||
                    app.packageName.contains("player") ||
                    app.packageName.contains("bilibili") ||
                    app.packageName.contains("iqiyi") ||
                    app.packageName.contains("tencent") ||
                    app.packageName.contains("youku") ||
                    app.packageName.contains("netflix") ||
                    app.packageName.contains("youtube")
                }

                _uiState.value = _uiState.value.copy(
                    videoApps = videoApps
                )
            }
        }
    }

    private fun monitorVehicleSpeed() {
        viewModelScope.launch {
            carControlService.carInfo.collect { carInfo ->
                val isMoving = carInfo.speed > 5f // 5 km/h threshold

                if (isMoving && _uiState.value.isPlaying) {
                    // Vehicle is moving, pause video
                    _uiState.value = _uiState.value.copy(
                        isPlaying = false,
                        safetyWarning = true,
                        lastSpeed = carInfo.speed
                    )
                } else if (!isMoving && _uiState.value.lastSpeed > 5f) {
                    // Vehicle stopped, can resume
                    _uiState.value = _uiState.value.copy(
                        lastSpeed = carInfo.speed
                    )
                }
            }
        }
    }

    fun playVideo(videoPath: String) {
        _uiState.value = _uiState.value.copy(
            isPlaying = true,
            currentVideo = videoPath,
            safetyWarning = false
        )
    }

    fun pauseVideo() {
        _uiState.value = _uiState.value.copy(
            isPlaying = false
        )
    }

    fun dismissSafetyWarning() {
        _uiState.value = _uiState.value.copy(
            safetyWarning = false
        )
    }

    fun enableAudioOnly() {
        _uiState.value = _uiState.value.copy(
            audioOnly = true,
            isPlaying = true
        )
    }

    fun disableSafetyRestriction(password: String) {
        // Verify password
        if (password == "8888") { // Default factory password
            _uiState.value = _uiState.value.copy(
                safetyRestricted = false
            )
        }
    }
}

data class VideoUiState(
    val videoApps: List<com.autohub.launcher.domain.model.AppInfo> = emptyList(),
    val isPlaying: Boolean = false,
    val currentVideo: String? = null,
    val safetyWarning: Boolean = false,
    val audioOnly: Boolean = false,
    val safetyRestricted: Boolean = true,
    val lastSpeed: Float = 0f,
    val isPipMode: Boolean = false
)
