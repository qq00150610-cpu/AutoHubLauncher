package com.autohub.launcher.ui.aircontrol

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.autohub.launcher.data.local.SettingsRepository
import com.autohub.launcher.service.CarControlService
import com.autohub.launcher.service.TemperatureZone
import com.autohub.launcher.domain.model.AirMode
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class AirControlViewModel @Inject constructor(
    private val carControlService: CarControlService,
    private val settingsRepository: SettingsRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(AirControlUiState())
    val uiState: StateFlow<AirControlUiState> = _uiState.asStateFlow()

    init {
        loadCarInfo()
        loadSettings()
    }

    private fun loadCarInfo() {
        viewModelScope.launch {
            carControlService.carInfo.collect { carInfo ->
                _uiState.value = _uiState.value.copy(
                    carInfo = carInfo
                )
            }
        }
    }

    private fun loadSettings() {
        viewModelScope.launch {
            settingsRepository.carModel.collect { carModel ->
                _uiState.value = _uiState.value.copy(
                    carModel = carModel,
                    isACSupported = carModel in listOf("BYD", "GEELY", "CHANGAN")
                )
            }
        }
    }

    fun onDriverTemperatureChanged(temperature: Int) {
        carControlService.setTemperature(TemperatureZone.DRIVER, temperature)
        _uiState.value = _uiState.value.copy(
            driverTemperature = temperature,
            passengerTemperature = if (_uiState.value.syncTemperatures) temperature else _uiState.value.passengerTemperature
        )
    }

    fun onPassengerTemperatureChanged(temperature: Int) {
        carControlService.setTemperature(TemperatureZone.PASSENGER, temperature)
        _uiState.value = _uiState.value.copy(
            passengerTemperature = temperature
        )
    }

    fun onFanSpeedChanged(speed: Int) {
        carControlService.setFanSpeed(speed)
        _uiState.value = _uiState.value.copy(
            fanSpeed = speed
        )
    }

    fun onAirModeChanged(mode: AirMode) {
        carControlService.setAirMode(mode)
        _uiState.value = _uiState.value.copy(
            airMode = mode
        )
    }

    fun onACEnabledChanged(enabled: Boolean) {
        carControlService.setACEnabled(enabled)
        _uiState.value = _uiState.value.copy(
            isACEnabled = enabled
        )
    }

    fun onRecirculationChanged(enabled: Boolean) {
        carControlService.setRecirculation(enabled)
        _uiState.value = _uiState.value.copy(
            isRecirculationEnabled = enabled
        )
    }

    fun onSyncTemperaturesChanged(enabled: Boolean) {
        if (enabled) {
            _uiState.value = _uiState.value.copy(
                passengerTemperature = _uiState.value.driverTemperature,
                syncTemperatures = enabled
            )
        } else {
            _uiState.value = _uiState.value.copy(
                syncTemperatures = enabled
            )
        }
    }

    fun onAutoModeChanged(enabled: Boolean) {
        _uiState.value = _uiState.value.copy(
            isAutoMode = enabled
        )
        if (enabled) {
            carControlService.setAirMode(AirMode.AUTO)
        }
    }

    fun toggleSeatVentilation() {
        _uiState.value = _uiState.value.copy(
            isSeatVentilationEnabled = !_uiState.value.isSeatVentilationEnabled
        )
    }

    fun toggleHeatedSteering() {
        _uiState.value = _uiState.value.copy(
            isHeatedSteeringEnabled = !_uiState.value.isHeatedSteeringEnabled
        )
    }

    fun toggleRearDefroster() {
        _uiState.value = _uiState.value.copy(
            isRearDefrosterEnabled = !_uiState.value.isRearDefrosterEnabled
        )
    }
}

data class AirControlUiState(
    val carInfo: com.autohub.launcher.domain.model.CarInfo? = null,
    val carModel: String = "GENERIC",
    val isACSupported: Boolean = false,
    
    // Temperature Controls
    val driverTemperature: Int = 24,
    val passengerTemperature: Int = 24,
    val syncTemperatures: Boolean = true,
    
    // Fan Controls
    val fanSpeed: Int = 2,
    val maxFanSpeed: Int = 6,
    
    // Air Mode
    val airMode: AirMode = AirMode.AUTO,
    
    // AC Controls
    val isACEnabled: Boolean = true,
    val isRecirculationEnabled: Boolean = false,
    val isAutoMode: Boolean = true,
    
    // Additional Controls
    val isSeatVentilationEnabled: Boolean = false,
    val isHeatedSteeringEnabled: Boolean = false,
    val isRearDefrosterEnabled: Boolean = false,
    
    // Temperature Range
    val minTemperature: Int = 16,
    val maxTemperature: Int = 32,
    
    val isLoading: Boolean = false,
    val error: String? = null
)
