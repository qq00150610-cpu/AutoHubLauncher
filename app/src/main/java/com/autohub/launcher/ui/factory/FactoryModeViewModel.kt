package com.autohub.launcher.ui.factory

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class FactoryModeViewModel @Inject constructor() : ViewModel() {

    private val _uiState = MutableStateFlow(FactoryModeUiState())
    val uiState: StateFlow<FactoryModeUiState> = _uiState.asStateFlow()

    fun onPasswordInput(password: String) {
        _uiState.value = _uiState.value.copy(
            password = password
        )
    }

    fun verifyPassword() {
        val inputPassword = _uiState.value.password
        val isValid = verifyFactoryPassword(inputPassword)
        
        _uiState.value = _uiState.value.copy(
            isPasswordVerified = isValid,
            passwordError = if (!isValid) "密码错误" else null
        )

        if (isValid) {
            loadFactoryData()
        }
    }

    private fun verifyFactoryPassword(password: String): Boolean {
        // Default factory passwords
        val validPasswords = listOf("8888", "3368", "123456", "888888")
        return password in validPasswords
    }

    private fun loadFactoryData() {
        viewModelScope.launch {
            // Load system information
            val systemInfo = getSystemInfo()
            _uiState.value = _uiState.value.copy(
                systemInfo = systemInfo
            )
        }
    }

    fun onFunctionSelected(function: FactoryFunction) {
        when (function) {
            FactoryFunction.SCREEN_TEST -> performScreenTest()
            FactoryFunction.AUDIO_TEST -> performAudioTest()
            FactoryFunction.BUTTON_TEST -> performButtonTest()
            FactoryFunction.CAMERA_TEST -> performCameraTest()
            FactoryFunction.WIFI_TEST -> performWifiTest()
            FactoryFunction.BLUETOOTH_TEST -> performBluetoothTest()
            FactoryFunction.SYSTEM_INFO -> loadFactoryData()
            FactoryFunction.LOG_EXPORT -> exportLogs()
            FactoryFunction.FACTORY_RESET -> confirmFactoryReset()
            FactoryFunction.SYSTEM_UPDATE -> checkSystemUpdate()
            FactoryFunction.DEVICE_INFO -> loadDeviceInfo()
        }
    }

    fun onFactoryResetConfirmed() {
        performFactoryReset()
    }

    fun dismissResetDialog() {
        _uiState.value = _uiState.value.copy(
            showResetConfirmDialog = false
        )
    }

    private fun performScreenTest() {
        _uiState.value = _uiState.value.copy(
            isRunningTest = true,
            currentTest = FactoryFunction.SCREEN_TEST
        )
        // Implement screen test logic
    }

    private fun performAudioTest() {
        _uiState.value = _uiState.value.copy(
            isRunningTest = true,
            currentTest = FactoryFunction.AUDIO_TEST
        )
        // Implement audio test logic
    }

    private fun performButtonTest() {
        _uiState.value = _uiState.value.copy(
            isRunningTest = true,
            currentTest = FactoryFunction.BUTTON_TEST
        )
        // Implement button test logic
    }

    private fun performCameraTest() {
        _uiState.value = _uiState.value.copy(
            isRunningTest = true,
            currentTest = FactoryFunction.CAMERA_TEST
        )
        // Implement camera test logic
    }

    private fun performWifiTest() {
        _uiState.value = _uiState.value.copy(
            isRunningTest = true,
            currentTest = FactoryFunction.WIFI_TEST
        )
        // Implement WiFi test logic
    }

    private fun performBluetoothTest() {
        _uiState.value = _uiState.value.copy(
            isRunningTest = true,
            currentTest = FactoryFunction.BLUETOOTH_TEST
        )
        // Implement Bluetooth test logic
    }

    private fun exportLogs() {
        _uiState.value = _uiState.value.copy(
            isExportingLogs = true
        )
        // Implement log export logic
    }

    private fun confirmFactoryReset() {
        _uiState.value = _uiState.value.copy(
            showResetConfirmDialog = true
        )
    }

    private fun performFactoryReset() {
        _uiState.value = _uiState.value.copy(
            isResetting = true,
            showResetConfirmDialog = false
        )
        // Implement factory reset logic
    }

    private fun checkSystemUpdate() {
        _uiState.value = _uiState.value.copy(
            isCheckingUpdate = true
        )
        // Implement system update check logic
    }

    private fun loadDeviceInfo() {
        val deviceInfo = getDeviceInfo()
        _uiState.value = _uiState.value.copy(
            deviceInfo = deviceInfo
        )
    }

    private fun getSystemInfo(): SystemInfo {
        return SystemInfo(
            androidVersion = android.os.Build.VERSION.RELEASE,
            buildNumber = android.os.Build.DISPLAY,
            kernelVersion = System.getProperty("os.version") ?: "",
            deviceModel = android.os.Build.MODEL,
            manufacturer = android.os.Build.MANUFACTURER,
            board = android.os.Build.BOARD,
            hardware = android.os.Build.HARDWARE
        )
    }

    private fun getDeviceInfo(): DeviceInfo {
        return DeviceInfo(
            model = android.os.Build.MODEL,
            manufacturer = android.os.Build.MANUFACTURER,
            serialNumber = android.os.Build.getSerial(),
            productId = android.os.Build.ID,
            fingerprint = android.os.Build.FINGERPRINT
        )
    }

    fun exitFactoryMode() {
        _uiState.value = FactoryModeUiState()
    }
}

data class FactoryModeUiState(
    val password: String = "",
    val isPasswordVerified: Boolean = false,
    val passwordError: String? = null,
    val systemInfo: SystemInfo? = null,
    val deviceInfo: DeviceInfo? = null,
    val isRunningTest: Boolean = false,
    val currentTest: FactoryFunction? = null,
    val isExportingLogs: Boolean = false,
    val isResetting: Boolean = false,
    val isCheckingUpdate: Boolean = false,
    val showResetConfirmDialog: Boolean = false,
    val testResults: Map<FactoryFunction, TestResult> = emptyMap()
)

data class SystemInfo(
    val androidVersion: String,
    val buildNumber: String,
    val kernelVersion: String,
    val deviceModel: String,
    val manufacturer: String,
    val board: String,
    val hardware: String
)

data class DeviceInfo(
    val model: String,
    val manufacturer: String,
    val serialNumber: String,
    val productId: String,
    val fingerprint: String
)

data class TestResult(
    val testName: String,
    val status: TestStatus,
    val details: String
)

enum class TestStatus {
    PASSED, FAILED, RUNNING, NOT_STARTED
}

enum class FactoryFunction {
    SCREEN_TEST,
    AUDIO_TEST,
    BUTTON_TEST,
    CAMERA_TEST,
    WIFI_TEST,
    BLUETOOTH_TEST,
    SYSTEM_INFO,
    LOG_EXPORT,
    FACTORY_RESET,
    SYSTEM_UPDATE,
    DEVICE_INFO
}
