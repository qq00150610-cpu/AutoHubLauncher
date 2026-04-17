package com.autohub.launcher.ui.settings

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.autohub.launcher.data.adapter.CarAdapter
import com.autohub.launcher.data.adapter.BYDCarAdapter
import com.autohub.launcher.data.adapter.GeelyCarAdapter
import com.autohub.launcher.data.adapter.DongfengAdapter
import com.autohub.launcher.data.adapter.GenericCarAdapter
import com.autohub.launcher.data.local.SettingsRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class SettingsViewModel @Inject constructor(
    private val settingsRepository: SettingsRepository,
    private val bydAdapter: BYDCarAdapter,
    private val geelyAdapter: GeelyCarAdapter,
    private val dongfengAdapter: DongfengAdapter,
    private val genericAdapter: GenericCarAdapter
) : ViewModel() {

    private val _uiState = MutableStateFlow(SettingsUiState())
    val uiState: StateFlow<SettingsUiState> = _uiState.asStateFlow()
    
    // 可用的车辆适配器列表
    val availableCarAdapters = listOf(
        dongfengAdapter,
        bydAdapter,
        geelyAdapter,
        genericAdapter
    )

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
                    selectedCarModel = model,
                    selectedManufacturer = extractManufacturerFromModel(model)
                )
            }
        }
    }

    private fun extractManufacturerFromModel(model: String): String {
        return when {
            model.contains("奕炫") || model.contains("AX7") || 
            model.contains("GS") || model.contains("皓极") -> "东风风神"
            model.contains("秦") || model.contains("汉") || 
            model.contains("唐") || model.contains("宋") ||
            model.contains("元") || model.contains("海豚") || 
            model.contains("海豹") -> "比亚迪"
            model.contains("博越") || model.contains("星越") || 
            model.contains("缤瑞") || model.contains("帝豪") ||
            model.contains("嘉际") || model.contains("远景") || 
            model.contains("豪越") -> "吉利"
            else -> "通用"
        }
    }

    fun selectCarModel(manufacturer: String, model: String) {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(
                selectedManufacturer = manufacturer,
                selectedCarModel = model
            )
            settingsRepository.setCarModel(model)
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
    val selectedCarModel: String = "通用车型",
    val selectedManufacturer: String = "通用",
    val appVersion: String = "1.0.0",
    val buildNumber: String = "1"
)
