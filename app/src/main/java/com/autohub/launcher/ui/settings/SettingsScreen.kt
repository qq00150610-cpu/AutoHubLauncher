package com.autohub.launcher.ui.settings

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel

@Composable
fun SettingsScreen(
    viewModel: SettingsViewModel = hiltViewModel(),
    onBack: () -> Unit = {}
) {
    val uiState by viewModel.uiState.collectAsState()

    Scaffold(
        modifier = Modifier.fillMaxSize(),
        topBar = {
            SettingsTopBar(onBack = onBack)
        }
    ) { paddingValues ->
        LazyColumn(
            modifier = Modifier
                .padding(paddingValues)
                .fillMaxSize(),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            item {
                SectionHeader("通用设置")
            }

            item {
                SettingToggleItem(
                    icon = Icons.Default.FiberManualRecord,
                    title = "悬浮球",
                    subtitle = "启用桌面悬浮球",
                    checked = uiState.floatingBallEnabled,
                    onCheckedChange = { viewModel.toggleFloatingBall(it) }
                )
            }

            item {
                SettingToggleItem(
                    icon = Icons.Default.Visibility,
                    title = "自动隐藏悬浮球",
                    subtitle = "3秒无操作后自动隐藏",
                    checked = uiState.autoHideFloatingBall,
                    onCheckedChange = { viewModel.toggleAutoHideFloatingBall(it) }
                )
            }

            item {
                SettingToggleItem(
                    icon = Icons.Default.DarkMode,
                    title = "深色模式",
                    subtitle = "启用深色主题",
                    checked = uiState.darkMode,
                    onCheckedChange = { viewModel.toggleDarkMode(it) }
                )
            }

            item {
                Spacer(modifier = Modifier.height(8.dp))
                SectionHeader("车辆设置")
            }

            item {
                SettingClickItem(
                    icon = Icons.Default.DirectionsCar,
                    title = "车型选择",
                    subtitle = uiState.selectedCarModel,
                    onClick = { /* Navigate to car model selection */ }
                )
            }

            item {
                SettingClickItem(
                    icon = Icons.Default.AcUnit,
                    title = "空调控制",
                    subtitle = "配置本机空调设置",
                    onClick = { /* Navigate to AC settings */ }
                )
            }

            item {
                SettingClickItem(
                    icon = Icons.Default.SettingsRemote,
                    title = "方控映射",
                    subtitle = "自定义方向盘按键",
                    onClick = { /* Navigate to key mapping */ }
                )
            }

            item {
                Spacer(modifier = Modifier.height(8.dp))
                SectionHeader("高级设置")
            }

            item {
                SettingClickItem(
                    icon = Icons.Default.Construction,
                    title = "工厂模式",
                    subtitle = "工程模式入口",
                    onClick = { /* Navigate to factory mode */ }
                )
            }

            item {
                SettingClickItem(
                    icon = Icons.Default.DeveloperMode,
                    title = "开发者选项",
                    subtitle = "ADB调试和开发者工具",
                    onClick = { /* Navigate to developer options */ }
                )
            }

            item {
                Spacer(modifier = Modifier.height(8.dp))
                SectionHeader("关于")
            }

            item {
                SettingInfoItem(
                    icon = Icons.Default.Info,
                    title = "版本信息",
                    subtitle = "版本 ${uiState.appVersion} (${uiState.buildNumber})"
                )
            }

            item {
                SettingClickItem(
                    icon = Icons.Default.Update,
                    title = "检查更新",
                    subtitle = "检查应用更新",
                    onClick = { /* Check for updates */ }
                )
            }

            item {
                Spacer(modifier = Modifier.height(16.dp))
            }
        }
    }
}

@Composable
private fun SettingsTopBar(
    onBack: () -> Unit
) {
    TopAppBar(
        title = { Text("设置", fontWeight = FontWeight.Bold) },
        navigationIcon = {
            IconButton(onClick = onBack) {
                Icon(Icons.Default.ArrowBack, contentDescription = "返回")
            }
        },
        colors = TopAppBarDefaults.topAppBarColors(
            containerColor = MaterialTheme.colorScheme.surface
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
        modifier = Modifier.padding(horizontal = 16.dp)
    )
}

@Composable
private fun SettingToggleItem(
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    title: String,
    subtitle: String,
    checked: Boolean,
    onCheckedChange: (Boolean) -> Unit
) {
    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp),
        shape = RoundedCornerShape(12.dp),
        color = MaterialTheme.colorScheme.surfaceVariant
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
                    imageVector = icon,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.primary
                )
                Column {
                    Text(
                        text = title,
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Medium
                    )
                    Text(
                        text = subtitle,
                        fontSize = 12.sp,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }
            Switch(
                checked = checked,
                onCheckedChange = onCheckedChange
            )
        }
    }
}

@Composable
private fun SettingClickItem(
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    title: String,
    subtitle: String,
    onClick: () -> Unit
) {
    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp)
            .clickable(onClick = onClick),
        shape = RoundedCornerShape(12.dp),
        color = MaterialTheme.colorScheme.surfaceVariant
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
                    imageVector = icon,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.primary
                )
                Column {
                    Text(
                        text = title,
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Medium
                    )
                    Text(
                        text = subtitle,
                        fontSize = 12.sp,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }
            Icon(
                Icons.Default.ChevronRight,
                contentDescription = "进入",
                tint = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}

@Composable
private fun SettingInfoItem(
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    title: String,
    subtitle: String
) {
    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp),
        shape = RoundedCornerShape(12.dp),
        color = MaterialTheme.colorScheme.surfaceVariant
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            horizontalArrangement = Arrangement.spacedBy(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                imageVector = icon,
                contentDescription = null,
                tint = MaterialTheme.colorScheme.primary
            )
            Column {
                Text(
                    text = title,
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Medium
                )
                Text(
                    text = subtitle,
                    fontSize = 12.sp,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }
    }
}
