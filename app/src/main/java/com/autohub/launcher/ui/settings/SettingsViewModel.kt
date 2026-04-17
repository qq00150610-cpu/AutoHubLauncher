package com.autohub.launcher.ui.settings

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.autohub.launcher.data.local.SettingsRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class SettingsViewModel @Inject constructor(
    private val settingsRepository: SettingsRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(SettingsUiState())
    val uiState: StateFlow<SettingsUiState> = _uiState.asStateFlow()

    init {
        loadSettings()
    }

    private fun loadSettings() {
        viewModelScope.launch {
            settingsRepository.floatingBallEnabled.collect { enabled ->
                _uiState.value = _uiState.value.copy(
                    floatingBallEnabled = enabled
                )
            }
        }

        viewModelScope.launch {
            settingsRepository.autoHideFloatingBall.collect { enabled ->
                _uiState.value = _uiState.value.copy(
                    autoHideFloatingBall = enabled
                )
            }
        }

        viewModelScope.launch {
            settingsRepository.darkMode.collect { enabled ->
                _uiState.value = _uiState.value.copy(
                    darkMode = enabled
                )
            }
        }

        viewModelScope.launch {
            settingsRepository.carModel.collect { model ->
                _uiState.value = _uiState.value.copy(
                    selectedCarModel = model
                )
            }
        }
    }

    fun toggleFloatingBall(enabled: Boolean) {
        viewModelScope.launch {
            settingsRepository.setFloatingBallEnabled(enabled)
        }
    }

    fun toggleAutoHideFloatingBall(enabled: Boolean) {
        viewModelScope.launch {
            settingsRepository.setAutoHideFloatingBall(enabled)
        }
    }

    fun toggleDarkMode(enabled: Boolean) {
        viewModelScope.launch {
            settingsRepository.setDarkMode(enabled)
        }
    }

    fun selectCarModel(model: String) {
        viewModelScope.launch {
            settingsRepository.setCarModel(model)
        }
    }
}

data class SettingsUiState(
    val floatingBallEnabled: Boolean = true,
    val autoHideFloatingBall: Boolean = true,
    val darkMode: Boolean = false,
    val selectedCarModel: String = "GENERIC",
    val appVersion: String = "1.0.0",
    val buildNumber: String = "1"
)
