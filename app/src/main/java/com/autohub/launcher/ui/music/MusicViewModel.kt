package com.autohub.launcher.ui.music

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.autohub.launcher.domain.usecase.GetInstalledAppsUseCase
import com.autohub.launcher.service.MusicControlService
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class MusicViewModel @Inject constructor(
    private val getInstalledAppsUseCase: GetInstalledAppsUseCase,
    private val musicService: MusicControlService
) : ViewModel() {

    private val _uiState = MutableStateFlow(MusicUiState())
    val uiState: StateFlow<MusicUiState> = _uiState.asStateFlow()

    init {
        loadMusicApps()
    }

    private fun loadMusicApps() {
        viewModelScope.launch {
            val allApps = getInstalledAppsUseCase()
            allApps.collect { apps ->
                val musicApps = apps.filter { app ->
                    app.packageName.contains("music") ||
                    app.packageName.contains("kuwo") ||
                    app.packageName.contains("kugou") ||
                    app.packageName.contains("qqmusic") ||
                    app.packageName.contains("netease") ||
                    app.packageName.contains("spotify")
                }

                _uiState.value = _uiState.value.copy(
                    musicApps = musicApps
                )
            }
        }
    }

    fun playPause() {
        val newState = _uiState.value.copy(
            isPlaying = !_uiState.value.isPlaying
        )
        _uiState.value = newState
        musicService.playPause()
    }

    fun next() {
        musicService.next()
    }

    fun previous() {
        musicService.previous()
    }

    fun onAppSelected(appPackageName: String) {
        // Launch music app
        _uiState.value = _uiState.value.copy(
            activeApp = appPackageName
        )
    }
}

data class MusicUiState(
    val musicApps: List<com.autohub.launcher.domain.model.AppInfo> = emptyList(),
    val isPlaying: Boolean = false,
    val activeApp: String? = null,
    val currentTrack: String? = null,
    val currentArtist: String? = null,
    val albumArt: String? = null,
    val progress: Float = 0f,
    val duration: Int = 0,
    val currentTime: Int = 0
)
