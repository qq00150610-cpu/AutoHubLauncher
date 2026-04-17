package com.autohub.launcher.ui.steeringwidget

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.autohub.launcher.data.local.SettingsRepository
import com.autohub.launcher.domain.model.SteeringWheelProfiles
import com.autohub.launcher.domain.model.SteeringWheelProfile
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class SteeringWidgetViewModel @Inject constructor(
    private val settingsRepository: SettingsRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(SteeringWidgetUiState())
    val uiState: StateFlow<SteeringWidgetUiState> = _uiState.asStateFlow()

    init {
        loadProfiles()
        loadCurrentProfile()
    }

    private fun loadProfiles() {
        viewModelScope.launch {
            val profiles = listOf(
                SteeringWheelProfiles.DEFAULT_PROFILE,
                SteeringWheelProfiles.MUSIC_PROFILE,
                SteeringWheelProfiles.NAVIGATION_PROFILE
            )
            _uiState.value = _uiState.value.copy(
                availableProfiles = profiles
            )
        }
    }

    private fun loadCurrentProfile() {
        viewModelScope.launch {
            settingsRepository.carModel.collect { carModel ->
                val currentProfile = when (carModel) {
                    "BYD" -> SteeringWheelProfiles.DEFAULT_PROFILE
                    "GEELY" -> SteeringWheelProfiles.MUSIC_PROFILE
                    else -> SteeringWheelProfiles.DEFAULT_PROFILE
                }
                _uiState.value = _uiState.value.copy(
                    currentProfile = currentProfile,
                    selectedCarModel = carModel
                )
            }
        }
    }

    fun onProfileSelected(profile: SteeringWheelProfile) {
        _uiState.value = _uiState.value.copy(
            currentProfile = profile,
            showMappingEditor = false
        )
    }

    fun onCreateCustomProfile(profileName: String) {
        val newProfile = SteeringWheelProfile(
            name = profileName,
            isDefault = false,
            mappings = _uiState.value.currentProfile?.mappings ?: emptyMap()
        )
        _uiState.value = _uiState.value.copy(
            currentProfile = newProfile,
            availableProfiles = _uiState.value.availableProfiles + newProfile,
            showCreateDialog = false
        )
    }

    fun onEditMapping(keyCode: Int) {
        _uiState.value = _uiState.value.copy(
            editingKeyCode = keyCode,
            showMappingEditor = true
        )
    }

    fun onMappingSelected(action: com.autohub.launcher.domain.model.KeyAction) {
        val currentProfile = _uiState.value.currentProfile ?: return
        val currentMappings = currentProfile.mappings.toMutableMap()
        val editingKey = currentMappings[_uiState.value.editingKeyCode]

        if (editingKey != null) {
            val updatedMapping = when (_uiState.value.selectedPressType) {
                PressType.SHORT -> editingKey.copy(shortPressAction = action)
                PressType.LONG -> editingKey.copy(longPressAction = action)
                PressType.DOUBLE_CLICK -> editingKey.copy(doubleClickAction = action)
            }

            currentMappings[_uiState.value.editingKeyCode] = updatedMapping

            val updatedProfile = currentProfile.copy(mappings = currentMappings)
            _uiState.value = _uiState.value.copy(
                currentProfile = updatedProfile,
                showMappingEditor = false,
                editingKeyCode = null
            )
        }
    }

    fun onPressTypeSelected(pressType: PressType) {
        _uiState.value = _uiState.value.copy(
            selectedPressType = pressType
        )
    }

    fun onToggleTestMode() {
        _uiState.value = _uiState.value.copy(
            isTestModeEnabled = !_uiState.value.isTestModeEnabled
        )
    }

    fun onTestKeyDetected(keyCode: Int) {
        _uiState.value = _uiState.value.copy(
            lastDetectedKey = keyCode,
            detectedKeyTimestamp = System.currentTimeMillis()
        )
    }

    fun onShowCreateDialog() {
        _uiState.value = _uiState.value.copy(
            showCreateDialog = true
        )
    }

    fun onDismissCreateDialog() {
        _uiState.value = _uiState.value.copy(
            showCreateDialog = false
        )
    }

    fun onShowMappingEditor() {
        _uiState.value = _uiState.value.copy(
            showMappingEditor = true
        )
    }

    fun onDismissMappingEditor() {
        _uiState.value = _uiState.value.copy(
            showMappingEditor = false,
            editingKeyCode = null
        )
    }

    fun onDeleteProfile(profile: SteeringWheelProfile) {
        if (!profile.isDefault) {
            val updatedProfiles = _uiState.value.availableProfiles.filter { it != profile }
            _uiState.value = _uiState.value.copy(
                availableProfiles = updatedProfiles
            )
        }
    }

    fun onExportProfile(profile: SteeringWheelProfile) {
        // Export profile logic
        _uiState.value = _uiState.value.copy(
            exportResult = "配置已导出"
        )
    }

    fun onImportProfile() {
        // Import profile logic
        _uiState.value = _uiState.value.copy(
            importResult = "配置已导入"
        )
    }

    fun dismissMessages() {
        _uiState.value = _uiState.value.copy(
            exportResult = null,
            importResult = null
        )
    }
}

data class SteeringWidgetUiState(
    val availableProfiles: List<SteeringWheelProfile> = emptyList(),
    val currentProfile: SteeringWheelProfile? = null,
    val selectedCarModel: String = "GENERIC",
    
    // UI States
    val showCreateDialog: Boolean = false,
    val showMappingEditor: Boolean = false,
    val editingKeyCode: Int? = null,
    val selectedPressType: PressType = PressType.SHORT,
    
    // Test Mode
    val isTestModeEnabled: Boolean = false,
    val lastDetectedKey: Int? = null,
    val detectedKeyTimestamp: Long? = null,
    
    // Export/Import
    val exportResult: String? = null,
    val importResult: String? = null
)

enum class PressType {
    SHORT, LONG, DOUBLE_CLICK
}
