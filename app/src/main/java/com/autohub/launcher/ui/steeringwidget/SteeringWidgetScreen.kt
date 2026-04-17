package com.autohub.launcher.ui.steeringwidget

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
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
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import com.autohub.launcher.domain.model.SteeringWheelProfile

@Composable
fun SteeringWidgetScreen(
    viewModel: SteeringWidgetViewModel = hiltViewModel(),
    onBack: () -> Unit = {}
) {
    val uiState by viewModel.uiState.collectAsState()

    Scaffold(
        modifier = Modifier.fillMaxSize(),
        topBar = {
            SteeringWidgetTopBar(
                onBack = onBack,
                onExport = { viewModel.onExportProfile(uiState.currentProfile ?: return) },
                onImport = { viewModel.onImportProfile() }
            )
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .padding(paddingValues)
                .fillMaxSize(),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // Profile Selection
            ProfileSelectionSection(
                profiles = uiState.availableProfiles,
                currentProfile = uiState.currentProfile,
                onProfileSelected = { viewModel.onProfileSelected(it) },
                onCreateProfile = { viewModel.onShowCreateDialog() }
            )

            // Test Mode Toggle
            TestModeSection(
                isEnabled = uiState.isTestModeEnabled,
                lastDetectedKey = uiState.lastDetectedKey,
                detectedKeyTimestamp = uiState.detectedKeyTimestamp,
                onToggle = { viewModel.onToggleTestMode() }
            )

            // Key Mappings
            uiState.currentProfile?.let { profile ->
                KeyMappingsSection(
                    mappings = profile.mappings.values.toList(),
                    onEditMapping = { viewModel.onEditMapping(it.keyCode) }
                )
            }
        }
    }

    // Create Profile Dialog
    if (uiState.showCreateDialog) {
        CreateProfileDialog(
            onDismiss = { viewModel.onDismissCreateDialog() },
            onCreate = { viewModel.onCreateCustomProfile(it) }
        )
    }

    // Mapping Editor Dialog
    if (uiState.showMappingEditor) {
        MappingEditorDialog(
            onDismiss = { viewModel.onDismissMappingEditor() },
            onPressTypeSelected = { viewModel.onPressTypeSelected(it) },
            onMappingSelected = { viewModel.onMappingSelected(it) },
            selectedPressType = uiState.selectedPressType
        )
    }

    // Export/Import Result Messages
    if (uiState.exportResult != null) {
        ResultMessageDialog(
            message = uiState.exportResult,
            onDismiss = { viewModel.dismissMessages() }
        )
    }

    if (uiState.importResult != null) {
        ResultMessageDialog(
            message = uiState.importResult,
            onDismiss = { viewModel.dismissMessages() }
        )
    }
}

@Composable
private fun SteeringWidgetTopBar(
    onBack: () -> Unit,
    onExport: () -> Unit,
    onImport: () -> Unit
) {
    TopAppBar(
        title = { Text("方控映射", fontWeight = FontWeight.Bold) },
        navigationIcon = {
            IconButton(onClick = onBack) {
                Icon(Icons.Default.ArrowBack, contentDescription = "返回")
            }
        },
        actions = {
            IconButton(onClick = onExport) {
                Icon(Icons.Default.Download, contentDescription = "导出配置")
            }
            IconButton(onClick = onImport) {
                Icon(Icons.Default.Upload, contentDescription = "导入配置")
            }
        },
        colors = TopAppBarDefaults.topAppBarColors(
            containerColor = MaterialTheme.colorScheme.surface
        )
    )
}

@Composable
private fun ProfileSelectionSection(
    profiles: List<SteeringWheelProfile>,
    currentProfile: SteeringWheelProfile?,
    onProfileSelected: (SteeringWheelProfile) -> Unit,
    onCreateProfile: () -> Unit
) {
    Column(
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        Text(
            text = "配置方案",
            fontSize = 18.sp,
            fontWeight = FontWeight.Bold
        )

        LazyColumn(
            modifier = Modifier.heightIn(max = 200.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            items(profiles) { profile ->
                ProfileItem(
                    profile = profile,
                    isSelected = profile == currentProfile,
                    onClick = { onProfileSelected(profile) },
                    onDelete = if (!profile.isDefault) {
                        { viewModel -> /* Delete logic */ }
                    } else null
                )
            }

            item {
                CreateProfileButton(onClick = onCreateProfile)
            }
        }
    }
}

@Composable
private fun ProfileItem(
    profile: SteeringWheelProfile,
    isSelected: Boolean,
    onClick: () -> Unit,
    onDelete: ((SteeringWidgetViewModel) -> Unit)? = null
) {
    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick),
        shape = RoundedCornerShape(12.dp),
        color = if (isSelected) {
            MaterialTheme.colorScheme.primaryContainer
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
                horizontalArrangement = Arrangement.spacedBy(12.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                RadioButton(
                    selected = isSelected,
                    onClick = onClick
                )

                Column {
                    Text(
                        text = profile.name,
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Medium
                    )

                    if (profile.isDefault) {
                        Text(
                            text = "默认配置",
                            fontSize = 12.sp,
                            color = MaterialTheme.colorScheme.primary
                        )
                    }

                    Text(
                        text = "${profile.mappings.size} 个按键映射",
                        fontSize = 12.sp,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }

            if (onDelete != null) {
                IconButton(onClick = { /* onDelete(viewModel) */ }) {
                    Icon(
                        Icons.Default.Delete,
                        contentDescription = "删除",
                        tint = MaterialTheme.colorScheme.error
                    )
                }
            }
        }
    }
}

@Composable
private fun CreateProfileButton(onClick: () -> Unit) {
    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick),
        shape = RoundedCornerShape(12.dp),
        color = MaterialTheme.colorScheme.surfaceVariant,
        border = androidx.compose.foundation.BorderStroke(
            2.dp,
            MaterialTheme.colorScheme.primary
        )
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            horizontalArrangement = Arrangement.Center,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                Icons.Default.Add,
                contentDescription = "创建配置",
                tint = MaterialTheme.colorScheme.primary
            )
            Spacer(modifier = Modifier.width(8.dp))
            Text(
                text = "创建新配置",
                color = MaterialTheme.colorScheme.primary,
                fontWeight = FontWeight.Medium
            )
        }
    }
}

@Composable
private fun TestModeSection(
    isEnabled: Boolean,
    lastDetectedKey: Int?,
    detectedKeyTimestamp: Long?,
    onToggle: () -> Unit
) {
    Surface(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(12.dp),
        color = if (isEnabled) {
            Color(0xFFFFF8E1)
        } else {
            MaterialTheme.colorScheme.surfaceVariant
        }
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        Icons.Default.TouchApp,
                        contentDescription = "测试模式",
                        tint = if (isEnabled) {
                            Color(0xFFFF9800)
                        } else {
                            MaterialTheme.colorScheme.onSurfaceVariant
                        }
                    )

                    Text(
                        text = "测试模式",
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold
                    )
                }

                Switch(
                    checked = isEnabled,
                    onCheckedChange = { onToggle() }
                )
            }

            if (isEnabled) {
                if (lastDetectedKey != null) {
                    TestKeyResult(
                        keyCode = lastDetectedKey,
                        timestamp = detectedKeyTimestamp
                    )
                } else {
                    Text(
                        text = "按下方向盘按键进行测试...",
                        fontSize = 14.sp,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        modifier = Modifier.fillMaxWidth(),
                        textAlign = TextAlign.Center
                    )
                }
            }
        }
    }
}

@Composable
private fun TestKeyResult(keyCode: Int, timestamp: Long?) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .background(Color(0xFFFFF3E0), RoundedCornerShape(8.dp))
            .padding(12.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            Icon(
                Icons.Default.Keyboard,
                contentDescription = "按键",
                modifier = Modifier.size(20.dp),
                tint = Color(0xFFFF9800)
            )
            Text(
                text = "检测到按键: $keyCode",
                fontSize = 14.sp,
                fontWeight = FontWeight.Medium
            )
        }

        timestamp?.let {
            Text(
                text = formatTimestamp(it),
                fontSize = 12.sp,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}

@Composable
private fun KeyMappingsSection(
    mappings: List<com.autohub.launcher.domain.model.SteeringWheelKeyMapping>,
    onEditMapping: (com.autohub.launcher.domain.model.SteeringWheelKeyMapping) -> Unit
) {
    Column(
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        Text(
            text = "按键映射",
            fontSize = 18.sp,
            fontWeight = FontWeight.Bold
        )

        LazyColumn(
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            items(mappings) { mapping ->
                KeyMappingItem(
                    mapping = mapping,
                    onClick = { onEditMapping(mapping) }
                )
            }
        }
    }
}

@Composable
private fun KeyMappingItem(
    mapping: com.autohub.launcher.domain.model.SteeringWheelKeyMapping,
    onClick: () -> Unit
) {
    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick),
        shape = RoundedCornerShape(12.dp),
        color = MaterialTheme.colorScheme.surfaceVariant
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Row(
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = mapping.name,
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold
                )
                Icon(
                    Icons.Default.ChevronRight,
                    contentDescription = "编辑",
                    tint = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }

            Row(
                horizontalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                ActionChipItem(
                    label = "短按",
                    action = mapping.shortPressAction
                )
                ActionChipItem(
                    label = "长按",
                    action = mapping.longPressAction
                )
                ActionChipItem(
                    label = "双击",
                    action = mapping.doubleClickAction
                )
            }
        }
    }
}

@Composable
private fun ActionChipItem(
    label: String,
    action: com.autohub.launcher.domain.model.KeyAction
) {
    AssistChip(
        onClick = {},
        label = {
            Text(
                text = "$label: ${getActionName(action)}",
                fontSize = 12.sp
            )
        }
    )
}

private fun getActionName(action: com.autohub.launcher.domain.model.KeyAction): String {
    return when (action) {
        is com.autohub.launcher.domain.model.KeyAction.LaunchApp -> "打开应用"
        is com.autohub.launcher.domain.model.KeyAction.MediaControl -> getMediaControlName(action)
        is com.autohub.launcher.domain.model.KeyAction.SystemAction -> getSystemActionName(action)
        is com.autohub.launcher.domain.model.KeyAction.NavigationAction -> getNavigationActionName(action)
        com.autohub.launcher.domain.model.KeyAction.None -> "无"
    }
}

private fun getMediaControlName(action: com.autohub.launcher.domain.model.KeyAction.MediaControl): String {
    return when (action.control) {
        com.autohub.launcher.domain.model.MediaControlAction.PLAY_PAUSE -> "播放/暂停"
        com.autohub.launcher.domain.model.MediaControlAction.NEXT -> "下一首"
        com.autohub.launcher.domain.model.MediaControlAction.PREVIOUS -> "上一首"
        com.autohub.launcher.domain.model.MediaControlAction.VOLUME_UP -> "音量+"
        com.autohub.launcher.domain.model.MediaControlAction.VOLUME_DOWN -> "音量-"
    }
}

private fun getSystemActionName(action: com.autohub.launcher.domain.model.KeyAction.SystemAction): String {
    return when (action.action) {
        com.autohub.launcher.domain.model.SystemActionType.HOME -> "回到桌面"
        com.autohub.launcher.domain.model.SystemActionType.BACK -> "返回"
        com.autohub.launcher.domain.model.SystemActionType.RECENT_APPS -> "最近应用"
        com.autohub.launcher.domain.model.SystemActionType.NOTIFICATION_PANEL -> "通知面板"
        com.autohub.launcher.domain.model.SystemActionType.SETTINGS -> "打开设置"
    }
}

private fun getNavigationActionName(action: com.autohub.launcher.domain.model.KeyAction.NavigationAction): String {
    return when (action.action) {
        com.autohub.launcher.domain.model.NavigationActionType.START_NAVIGATION -> "开始导航"
        com.autohub.launcher.domain.model.NavigationActionType.STOP_NAVIGATION -> "停止导航"
        com.autohub.launcher.domain.model.NavigationActionType.HOME_NAVIGATION -> "导航回家"
        com.autohub.launcher.domain.model.NavigationActionType.WORK_NAVIGATION -> "导航到公司"
    }
}

@Composable
private fun CreateProfileDialog(
    onDismiss: () -> Unit,
    onCreate: (String) -> Unit
) {
    var profileName by androidx.compose.runtime.remember { androidx.compose.runtime.mutableStateOf("") }

    Dialog(
        onDismissRequest = onDismiss,
        properties = DialogProperties(dismissOnBackPress = false, dismissOnClickOutside = false)
    ) {
        Surface(
            shape = RoundedCornerShape(16.dp),
            color = MaterialTheme.colorScheme.surface
        ) {
            Column(
                modifier = Modifier.padding(24.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                Text(
                    text = "创建新配置",
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Bold
                )

                OutlinedTextField(
                    value = profileName,
                    onValueChange = { profileName = it },
                    label = { Text("配置名称") },
                    singleLine = true
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
                        onClick = { onCreate(profileName) },
                        modifier = Modifier.weight(1f),
                        enabled = profileName.isNotBlank()
                    ) {
                        Text("创建")
                    }
                }
            }
        }
    }
}

@Composable
private fun MappingEditorDialog(
    onDismiss: () -> Unit,
    onPressTypeSelected: (PressType) -> Unit,
    onMappingSelected: (com.autohub.launcher.domain.model.KeyAction) -> Unit,
    selectedPressType: PressType
) {
    Dialog(
        onDismissRequest = onDismiss,
        properties = DialogProperties(dismissOnBackPress = false, dismissOnClickOutside = false)
    ) {
        Surface(
            shape = RoundedCornerShape(16.dp),
            color = MaterialTheme.colorScheme.surface
        ) {
            Column(
                modifier = Modifier.padding(24.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                Text(
                    text = "选择按键操作",
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Bold
                )

                // Press Type Selection
                Text(
                    text = "按键类型",
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Medium
                )

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    PressTypeButton(
                        label = "短按",
                        isSelected = selectedPressType == PressType.SHORT,
                        onClick = { onPressTypeSelected(PressType.SHORT) },
                        modifier = Modifier.weight(1f)
                    )
                    PressTypeButton(
                        label = "长按",
                        isSelected = selectedPressType == PressType.LONG,
                        onClick = { onPressTypeSelected(PressType.LONG) },
                        modifier = Modifier.weight(1f)
                    )
                    PressTypeButton(
                        label = "双击",
                        isSelected = selectedPressType == PressType.DOUBLE_CLICK,
                        onClick = { onPressTypeSelected(PressType.DOUBLE_CLICK) },
                        modifier = Modifier.weight(1f)
                    )
                }

                // Action Selection
                Text(
                    text = "选择操作",
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Medium
                )

                LazyColumn(
                    modifier = Modifier.heightIn(max = 300.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    // Media Control Actions
                    item {
                        Text(
                            text = "媒体控制",
                            fontSize = 14.sp,
                            color = MaterialTheme.colorScheme.primary
                        )
                    }

                    items(listOf(
                        com.autohub.launcher.domain.model.MediaControlAction.PLAY_PAUSE,
                        com.autohub.launcher.domain.model.MediaControlAction.NEXT,
                        com.autohub.launcher.domain.model.MediaControlAction.PREVIOUS,
                        com.autohub.launcher.domain.model.MediaControlAction.VOLUME_UP,
                        com.autohub.launcher.domain.model.MediaControlAction.VOLUME_DOWN
                    )) { action ->
                        ActionItem(
                            name = getMediaControlName(com.autohub.launcher.domain.model.KeyAction.MediaControl(action)),
                            onClick = { onMappingSelected(com.autohub.launcher.domain.model.KeyAction.MediaControl(action)) }
                        )
                    }

                    // System Actions
                    item {
                        Text(
                            text = "系统操作",
                            fontSize = 14.sp,
                            color = MaterialTheme.colorScheme.primary
                        )
                    }

                    items(listOf(
                        com.autohub.launcher.domain.model.SystemActionType.HOME,
                        com.autohub.launcher.domain.model.SystemActionType.BACK,
                        com.autohub.launcher.domain.model.SystemActionType.RECENT_APPS
                    )) { action ->
                        ActionItem(
                            name = getSystemActionName(com.autohub.launcher.domain.model.KeyAction.SystemAction(action)),
                            onClick = { onMappingSelected(com.autohub.launcher.domain.model.KeyAction.SystemAction(action)) }
                        )
                    }
                }

                OutlinedButton(
                    onClick = onDismiss,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text("取消")
                }
            }
        }
    }
}

@Composable
private fun PressTypeButton(
    label: String,
    isSelected: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    FilterChip(
        selected = isSelected,
        onClick = onClick,
        label = { Text(label) },
        modifier = modifier
    )
}

@Composable
private fun ActionItem(
    name: String,
    onClick: () -> Unit
) {
    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick),
        shape = RoundedCornerShape(8.dp),
        color = MaterialTheme.colorScheme.surfaceVariant
    ) {
        Text(
            text = name,
            modifier = Modifier.padding(12.dp),
            fontSize = 14.sp
        )
    }
}

@Composable
private fun ResultMessageDialog(
    message: String,
    onDismiss: () -> Unit
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("提示") },
        text = { Text(message) },
        confirmButton = {
            TextButton(onClick = onDismiss) {
                Text("确定")
            }
        }
    )
}

private fun formatTimestamp(timestamp: Long): String {
    val now = System.currentTimeMillis()
    val diff = now - timestamp
    return "${diff}ms 前"
}
