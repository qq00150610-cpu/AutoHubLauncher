package com.autohub.launcher.ui.factory

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import androidx.hilt.navigation.compose.hiltViewModel

@Composable
fun FactoryModeScreen(
    viewModel: FactoryModeViewModel = hiltViewModel(),
    onBack: () -> Unit = {}
) {
    val uiState by viewModel.uiState.collectAsState()

    if (!uiState.isPasswordVerified) {
        PasswordVerificationScreen(
            password = uiState.password,
            passwordError = uiState.passwordError,
            onPasswordChanged = { viewModel.onPasswordInput(it) },
            onVerifyPassword = { viewModel.verifyPassword() },
            onBack = onBack
        )
    } else {
        FactoryModeMainScreen(
            viewModel = viewModel,
            onBack = {
                viewModel.exitFactoryMode()
                onBack()
            }
        )
    }

    if (uiState.showResetConfirmDialog) {
        FactoryResetConfirmationDialog(
            onConfirm = { viewModel.onFactoryResetConfirmed() },
            onDismiss = { viewModel.dismissResetDialog() }
        )
    }
}

@Composable
private fun PasswordVerificationScreen(
    password: String,
    passwordError: String?,
    onPasswordChanged: (String) -> Unit,
    onVerifyPassword: () -> Unit,
    onBack: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(32.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(24.dp, Alignment.CenterVertically)
    ) {
        // Warning Icon
        Box(
            modifier = Modifier
                .size(80.dp)
                .background(Color(0xFFFF9800), CircleShape),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                Icons.Default.Lock,
                contentDescription = "工厂模式",
                modifier = Modifier.size(40.dp),
                tint = Color.White
            )
        }

        Text(
            text = "工厂模式",
            fontSize = 28.sp,
            fontWeight = FontWeight.Bold
        )

        Text(
            text = "仅限技术人员使用",
            fontSize = 14.sp,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )

        OutlinedTextField(
            value = password,
            onValueChange = onPasswordChanged,
            modifier = Modifier.fillMaxWidth(),
            label = { Text("请输入工厂模式密码") },
            placeholder = { Text("输入密码") },
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
            isError = passwordError != null,
            singleLine = true
        )

        if (passwordError != null) {
            Text(
                text = passwordError,
                color = MaterialTheme.colorScheme.error,
                fontSize = 14.sp
            )
        }

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Button(
                onClick = onBack,
                modifier = Modifier.weight(1f)
            ) {
                Text("取消")
            }

            Button(
                onClick = onVerifyPassword,
                modifier = Modifier.weight(1f),
                colors = ButtonDefaults.buttonColors(
                    containerColor = Color(0xFFFF9800)
                )
            ) {
                Text("进入")
            }
        }
    }
}

@Composable
private fun FactoryModeMainScreen(
    viewModel: FactoryModeViewModel,
    onBack: () -> Unit
) {
    val uiState by viewModel.uiState.collectAsState()

    Scaffold(
        modifier = Modifier.fillMaxSize(),
        topBar = {
            FactoryModeTopBar(
                onBack = onBack,
                onExit = { viewModel.exitFactoryMode() }
            )
        }
    ) { paddingValues ->
        LazyColumn(
            modifier = Modifier
                .padding(paddingValues)
                .fillMaxSize(),
            contentPadding = PaddingValues(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            item {
                SectionHeader("硬件测试")
            }

            items(FactoryFunction.values().filter { it.isHardwareTest() }) { function ->
                FactoryFunctionItem(
                    function = function,
                    isRunning = uiState.currentTest == function,
                    testResult = uiState.testResults[function],
                    onClick = { viewModel.onFunctionSelected(function) }
                )
            }

            item {
                Spacer(modifier = Modifier.height(16.dp))
                SectionHeader("系统信息")
            }

            if (uiState.systemInfo != null) {
                item {
                    SystemInfoCard(uiState.systemInfo)
                }
            }

            if (uiState.deviceInfo != null) {
                item {
                    DeviceInfoCard(uiState.deviceInfo)
                }
            }

            item {
                Spacer(modifier = Modifier.height(16.dp))
                SectionHeader("系统操作")
            }

            items(FactoryFunction.values().filter { it.isSystemOperation() }) { function ->
                FactoryFunctionItem(
                    function = function,
                    isRunning = false,
                    testResult = uiState.testResults[function],
                    onClick = { viewModel.onFunctionSelected(function) },
                    isDangerous = function == FactoryFunction.FACTORY_RESET
                )
            }

            item {
                Spacer(modifier = Modifier.height(32.dp))
            }
        }
    }
}

@Composable
private fun FactoryModeTopBar(
    onBack: () -> Unit,
    onExit: () -> Unit
) {
    TopAppBar(
        title = { Text("工厂模式", fontWeight = FontWeight.Bold) },
        navigationIcon = {
            IconButton(onClick = onBack) {
                Icon(Icons.Default.ArrowBack, contentDescription = "返回")
            }
        },
        actions = {
            IconButton(onClick = onExit) {
                Icon(Icons.Default.ExitToApp, contentDescription = "退出")
            }
        },
        colors = TopAppBarDefaults.topAppBarColors(
            containerColor = Color(0xFFFF9800)
        )
    )
}

@Composable
private fun SectionHeader(title: String) {
    Text(
        text = title,
        fontSize = 14.sp,
        color = MaterialTheme.colorScheme.primary,
        fontWeight = FontWeight.Bold,
        modifier = Modifier.padding(vertical = 8.dp)
    )
}

@Composable
private fun FactoryFunctionItem(
    function: FactoryFunction,
    isRunning: Boolean,
    testResult: TestResult?,
    onClick: () -> Unit,
    isDangerous: Boolean = false
) {
    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick),
        shape = RoundedCornerShape(12.dp),
        color = if (isDangerous) {
            Color(0xFFFFEBEE)
        } else {
            MaterialTheme.colorScheme.surfaceVariant
        }
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Row(
                horizontalArrangement = Arrangement.spacedBy(16.dp),
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.weight(1f)
            ) {
                Icon(
                    getFunctionIcon(function),
                    contentDescription = null,
                    tint = if (isDangerous) {
                        Color(0xFFF44336)
                    } else {
                        MaterialTheme.colorScheme.primary
                    }
                )
                Column {
                    Text(
                        text = getFunctionName(function),
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Medium
                    )
                    if (testResult != null) {
                        Text(
                            text = getTestStatusText(testResult.status),
                            fontSize = 12.sp,
                            color = when (testResult.status) {
                                TestStatus.PASSED -> Color(0xFF4CAF50)
                                TestStatus.FAILED -> Color(0xFFF44336)
                                TestStatus.RUNNING -> Color(0xFFFF9800)
                                TestStatus.NOT_STARTED -> MaterialTheme.colorScheme.onSurfaceVariant
                            }
                        )
                    }
                }
            }

            if (isRunning) {
                CircularProgressIndicator(modifier = Modifier.size(24.dp))
            } else {
                Icon(
                    Icons.Default.ChevronRight,
                    contentDescription = "进入",
                    tint = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }
    }
}

@Composable
private fun SystemInfoCard(systemInfo: com.autohub.launcher.ui.factory.SystemInfo) {
    Surface(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(12.dp),
        color = MaterialTheme.colorScheme.surfaceVariant
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            InfoRow("Android版本", systemInfo.androidVersion)
            InfoRow("构建号", systemInfo.buildNumber)
            InfoRow("内核版本", systemInfo.kernelVersion)
            InfoRow("设备型号", systemInfo.deviceModel)
            InfoRow("制造商", systemInfo.manufacturer)
            InfoRow("主板", systemInfo.board)
            InfoRow("硬件", systemInfo.hardware)
        }
    }
}

@Composable
private fun DeviceInfoCard(deviceInfo: com.autohub.launcher.ui.factory.DeviceInfo) {
    Surface(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(12.dp),
        color = MaterialTheme.colorScheme.surfaceVariant
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            InfoRow("型号", deviceInfo.model)
            InfoRow("制造商", deviceInfo.manufacturer)
            InfoRow("序列号", deviceInfo.serialNumber)
            InfoRow("产品ID", deviceInfo.productId)
            InfoRow("指纹", deviceInfo.fingerprint)
        }
    }
}

@Composable
private fun InfoRow(label: String, value: String) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(
            text = label,
            fontSize = 14.sp,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
        Text(
            text = value,
            fontSize = 14.sp,
            fontWeight = FontWeight.Medium
        )
    }
}

@Composable
private fun FactoryResetConfirmationDialog(
    onConfirm: () -> Unit,
    onDismiss: () -> Unit
) {
    Dialog(
        onDismissRequest = onDismiss,
        properties = DialogProperties(
            dismissOnBackPress = false,
            dismissOnClickOutside = false
        )
    ) {
        Surface(
            shape = RoundedCornerShape(16.dp),
            color = MaterialTheme.colorScheme.surface
        ) {
            Column(
                modifier = Modifier.padding(24.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                Icon(
                    Icons.Default.Warning,
                    contentDescription = "警告",
                    modifier = Modifier.size(48.dp),
                    tint = Color(0xFFF44336)
                )

                Text(
                    text = "⚠️ 警告",
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Bold
                )

                Text(
                    text = "恢复出厂设置将清除所有用户数据，包括应用、设置和个人文件。此操作不可撤销！",
                    fontSize = 14.sp,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    OutlinedButton(
                        onClick = onDismiss,
                        modifier = Modifier.weight(1f)
                    ) {
                        Text("取消")
                    }

                    Button(
                        onClick = onConfirm,
                        modifier = Modifier.weight(1f),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = Color(0xFFF44336)
                        )
                    ) {
                        Text("确认恢复")
                    }
                }
            }
        }
    }
}

private fun getFunctionIcon(function: FactoryFunction): androidx.compose.ui.graphics.vector.ImageVector {
    return when (function) {
        FactoryFunction.SCREEN_TEST -> Icons.Default.PhoneAndroid
        FactoryFunction.AUDIO_TEST -> Icons.Default.VolumeUp
        FactoryFunction.BUTTON_TEST -> Icons.Default.Keyboard
        FactoryFunction.CAMERA_TEST -> Icons.Default.CameraAlt
        FactoryFunction.WIFI_TEST -> Icons.Default.Wifi
        FactoryFunction.BLUETOOTH_TEST -> Icons.Default.Bluetooth
        FactoryFunction.SYSTEM_INFO -> Icons.Default.Info
        FactoryFunction.LOG_EXPORT -> Icons.Default.Download
        FactoryFunction.FACTORY_RESET -> Icons.Default.Restore
        FactoryFunction.SYSTEM_UPDATE -> Icons.Default.SystemUpdate
        FactoryFunction.DEVICE_INFO -> Icons.Default.DeviceInfo
    }
}

private fun getFunctionName(function: FactoryFunction): String {
    return when (function) {
        FactoryFunction.SCREEN_TEST -> "屏幕测试"
        FactoryFunction.AUDIO_TEST -> "音频测试"
        FactoryFunction.BUTTON_TEST -> "按键测试"
        FactoryFunction.CAMERA_TEST -> "摄像头测试"
        FactoryFunction.WIFI_TEST -> "WiFi测试"
        FactoryFunction.BLUETOOTH_TEST -> "蓝牙测试"
        FactoryFunction.SYSTEM_INFO -> "系统信息"
        FactoryFunction.LOG_EXPORT -> "日志导出"
        FactoryFunction.FACTORY_RESET -> "恢复出厂设置"
        FactoryFunction.SYSTEM_UPDATE -> "系统更新"
        FactoryFunction.DEVICE_INFO -> "设备信息"
    }
}

private fun getTestStatusText(status: TestStatus): String {
    return when (status) {
        TestStatus.PASSED -> "✓ 通过"
        TestStatus.FAILED -> "✗ 失败"
        TestStatus.RUNNING -> "运行中..."
        TestStatus.NOT_STARTED -> "未开始"
    }
}

private fun FactoryFunction.isHardwareTest(): Boolean {
    return this in listOf(
        FactoryFunction.SCREEN_TEST,
        FactoryFunction.AUDIO_TEST,
        FactoryFunction.BUTTON_TEST,
        FactoryFunction.CAMERA_TEST,
        FactoryFunction.WIFI_TEST,
        FactoryFunction.BLUETOOTH_TEST
    )
}

private fun FactoryFunction.isSystemOperation(): Boolean {
    return this in listOf(
        FactoryFunction.SYSTEM_INFO,
        FactoryFunction.LOG_EXPORT,
        FactoryFunction.FACTORY_RESET,
        FactoryFunction.SYSTEM_UPDATE,
        FactoryFunction.DEVICE_INFO
    )
}
