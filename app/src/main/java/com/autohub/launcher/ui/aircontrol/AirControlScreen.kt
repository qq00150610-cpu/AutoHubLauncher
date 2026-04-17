package com.autohub.launcher.ui.aircontrol

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
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
import com.autohub.launcher.service.AirMode

@Composable
fun AirControlScreen(
    viewModel: AirControlViewModel = hiltViewModel(),
    onBack: () -> Unit = {}
) {
    val uiState by viewModel.uiState.collectAsState()

    Scaffold(
        modifier = Modifier.fillMaxSize(),
        topBar = {
            AirControlTopBar(
                carModel = uiState.carModel,
                onBack = onBack
            )
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .padding(paddingValues)
                .fillMaxSize()
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(24.dp)
        ) {
            if (!uiState.isACSupported) {
                UnsupportedACCard()
            } else {
                TemperatureControlSection(
                    driverTemp = uiState.driverTemperature,
                    passengerTemp = uiState.passengerTemperature,
                    syncTemperatures = uiState.syncTemperatures,
                    minTemp = uiState.minTemperature,
                    maxTemp = uiState.maxTemperature,
                    onDriverTempChanged = { viewModel.onDriverTemperatureChanged(it) },
                    onPassengerTempChanged = { viewModel.onPassengerTemperatureChanged(it) },
                    onSyncToggle = { viewModel.onSyncTemperaturesChanged(it) }
                )

                FanControlSection(
                    fanSpeed = uiState.fanSpeed,
                    maxFanSpeed = uiState.maxFanSpeed,
                    isAutoMode = uiState.isAutoMode,
                    onSpeedChanged = { viewModel.onFanSpeedChanged(it) },
                    onAutoToggle = { viewModel.onAutoModeChanged(it) }
                )

                AirModeSection(
                    airMode = uiState.airMode,
                    onModeChanged = { viewModel.onAirModeChanged(it) }
                )

                QuickControlsSection(
                    isACEnabled = uiState.isACEnabled,
                    isRecirculationEnabled = uiState.isRecirculationEnabled,
                    isSeatVentilationEnabled = uiState.isSeatVentilationEnabled,
                    isHeatedSteeringEnabled = uiState.isHeatedSteeringEnabled,
                    isRearDefrosterEnabled = uiState.isRearDefrosterEnabled,
                    onACToggle = { viewModel.onACEnabledChanged(it) },
                    onRecirculationToggle = { viewModel.onRecirculationChanged(it) },
                    onSeatVentilationToggle = { viewModel.toggleSeatVentilation() },
                    onHeatedSteeringToggle = { viewModel.toggleHeatedSteering() },
                    onRearDefrosterToggle = { viewModel.toggleRearDefroster() }
                )
            }
        }
    }
}

@Composable
private fun AirControlTopBar(
    carModel: String,
    onBack: () -> Unit
) {
    TopAppBar(
        title = { 
            Column {
                Text("空调控制", fontWeight = FontWeight.Bold)
                Text(
                    text = "当前车型: $carModel",
                    fontSize = 12.sp,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        },
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
private fun UnsupportedACCard() {
    Surface(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        color = Color(0xFFFFF3E0)
    ) {
        Column(
            modifier = Modifier.padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Icon(
                Icons.Default.Air,
                contentDescription = "空调",
                modifier = Modifier.size(64.dp),
                tint = Color(0xFFFF9800)
            )

            Text(
                text = "当前车型不支持空调控制",
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold
            )

            Text(
                text = "请在设置中选择适配的车型",
                fontSize = 14.sp,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}

@Composable
private fun TemperatureControlSection(
    driverTemp: Int,
    passengerTemp: Int,
    syncTemperatures: Boolean,
    minTemp: Int,
    maxTemp: Int,
    onDriverTempChanged: (Int) -> Unit,
    onPassengerTempChanged: (Int) -> Unit,
    onSyncToggle: (Boolean) -> Unit
) {
    Surface(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        color = MaterialTheme.colorScheme.surfaceVariant
    ) {
        Column(
            modifier = Modifier.padding(20.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "温度控制",
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold
                )

                Row(
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "温度同步",
                        fontSize = 14.sp
                    )
                    Switch(
                        checked = syncTemperatures,
                        onCheckedChange = onSyncToggle
                    )
                }
            }

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                TemperatureControlCard(
                    title = "主驾",
                    temperature = driverTemp,
                    minTemp = minTemp,
                    maxTemp = maxTemp,
                    enabled = true,
                    onTemperatureChanged = onDriverTempChanged,
                    modifier = Modifier.weight(1f)
                )

                TemperatureControlCard(
                    title = "副驾",
                    temperature = passengerTemp,
                    minTemp = minTemp,
                    maxTemp = maxTemp,
                    enabled = !syncTemperatures,
                    onTemperatureChanged = onPassengerTempChanged,
                    modifier = Modifier.weight(1f)
                )
            }
        }
    }
}

@Composable
private fun TemperatureControlCard(
    title: String,
    temperature: Int,
    minTemp: Int,
    maxTemp: Int,
    enabled: Boolean,
    onTemperatureChanged: (Int) -> Unit,
    modifier: Modifier = Modifier
) {
    Surface(
        modifier = modifier.fillMaxWidth(),
        shape = RoundedCornerShape(12.dp),
        color = if (enabled) {
            MaterialTheme.colorScheme.surface
        } else {
            MaterialTheme.colorScheme.surface.copy(alpha = 0.5f)
        }
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Text(
                text = title,
                fontSize = 16.sp,
                fontWeight = FontWeight.Medium
            )

            TemperatureDisplay(
                temperature = temperature,
                enabled = enabled
            )

            TemperatureControls(
                temperature = temperature,
                minTemp = minTemp,
                maxTemp = maxTemp,
                enabled = enabled,
                onTemperatureChanged = onTemperatureChanged
            )
        }
    }
}

@Composable
private fun TemperatureDisplay(
    temperature: Int,
    enabled: Boolean
) {
    Box(
        modifier = Modifier.size(120.dp),
        contentAlignment = Alignment.Center
    ) {
        CircleProgress(
            progress = (temperature - 16f) / 16f, // Normalize to 0-1
            color = if (enabled) {
                MaterialTheme.colorScheme.primary
            } else {
                MaterialTheme.colorScheme.onSurface.copy(alpha = 0.3f)
            }
        )

        Column(
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = "${temperature}°C",
                fontSize = 28.sp,
                fontWeight = FontWeight.Bold,
                color = if (enabled) {
                    MaterialTheme.colorScheme.onSurface
                } else {
                    MaterialTheme.colorScheme.onSurface.copy(alpha = 0.3f)
                }
            )
        }
    }
}

@Composable
private fun CircleProgress(
    progress: Float,
    color: Color
) {
    androidx.compose.foundation.Canvas(
        modifier = Modifier.size(120.dp),
        onDraw = {
            val strokeWidth = 8.dp.toPx()
            val radius = (size.minDimension - strokeWidth) / 2

            // Background circle
            drawCircle(
                color = color.copy(alpha = 0.2f),
                radius = radius,
                style = androidx.compose.ui.graphics.drawscope.Stroke(width = strokeWidth)
            )

            // Progress arc
            drawArc(
                color = color,
                startAngle = -90f,
                sweepAngle = 360f * progress,
                useCenter = false,
                style = androidx.compose.ui.graphics.drawscope.Stroke(width = strokeWidth),
                size = androidx.compose.ui.geometry.Size(radius * 2, radius * 2),
                topLeft = androidx.compose.ui.geometry.Offset(
                    size.width / 2 - radius,
                    size.height / 2 - radius
                )
            )
        }
    )
}

@Composable
private fun TemperatureControls(
    temperature: Int,
    minTemp: Int,
    maxTemp: Int,
    enabled: Boolean,
    onTemperatureChanged: (Int) -> Unit
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceEvenly
    ) {
        TemperatureButton(
            icon = Icons.Default.Remove,
            onClick = {
                if (enabled && temperature > minTemp) {
                    onTemperatureChanged(temperature - 1)
                }
            },
            enabled = enabled && temperature > minTemp
        )

        TemperatureButton(
            icon = Icons.Default.Add,
            onClick = {
                if (enabled && temperature < maxTemp) {
                    onTemperatureChanged(temperature + 1)
                }
            },
            enabled = enabled && temperature < maxTemp
        )
    }
}

@Composable
private fun TemperatureButton(
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    onClick: () -> Unit,
    enabled: Boolean
) {
    IconButton(
        onClick = onClick,
        enabled = enabled,
        modifier = Modifier.size(48.dp)
    ) {
        Icon(
            icon,
            contentDescription = null,
            modifier = Modifier.size(32.dp),
            tint = if (enabled) {
                MaterialTheme.colorScheme.primary
            } else {
                MaterialTheme.colorScheme.onSurface.copy(alpha = 0.3f)
            }
        )
    }
}

@Composable
private fun FanControlSection(
    fanSpeed: Int,
    maxFanSpeed: Int,
    isAutoMode: Boolean,
    onSpeedChanged: (Int) -> Unit,
    onAutoToggle: (Boolean) -> Unit
) {
    Surface(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        color = MaterialTheme.colorScheme.surfaceVariant
    ) {
        Column(
            modifier = Modifier.padding(20.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "风速控制",
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold
                )

                Row(
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "自动模式",
                        fontSize = 14.sp
                    )
                    Switch(
                        checked = isAutoMode,
                        onCheckedChange = onAutoToggle
                    )
                }
            }

            FanSpeedControl(
                currentSpeed = fanSpeed,
                maxSpeed = maxFanSpeed,
                onSpeedChanged = onSpeedChanged
            )
        }
    }
}

@Composable
private fun FanSpeedControl(
    currentSpeed: Int,
    maxSpeed: Int,
    onSpeedChanged: (Int) -> Unit
) {
    Column(
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text(
                text = "风速",
                fontSize = 14.sp,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
            Text(
                text = "${currentSpeed}档",
                fontSize = 14.sp,
                fontWeight = FontWeight.Bold
            )
        }

        Slider(
            value = currentSpeed.toFloat(),
            onValueChange = { onSpeedChanged(it.toInt()) },
            valueRange = 0f..maxSpeed.toFloat(),
            steps = maxSpeed + 1,
            modifier = Modifier.fillMaxWidth()
        )

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            (0..maxSpeed).forEach { speed ->
                FilterChip(
                    selected = currentSpeed == speed,
                    onClick = { onSpeedChanged(speed) },
                    label = { Text(speed.toString()) },
                    modifier = Modifier.weight(1f)
                )
            }
        }
    }
}

@Composable
private fun AirModeSection(
    airMode: AirMode,
    onModeChanged: (AirMode) -> Unit
) {
    Surface(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        color = MaterialTheme.colorScheme.surfaceVariant
    ) {
        Column(
            modifier = Modifier.padding(20.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Text(
                text = "出风模式",
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold
            )

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                AirModeButton(
                    icon = Icons.Default.Face,
                    label = "面部",
                    selected = airMode == AirMode.FACE,
                    onClick = { onModeChanged(AirMode.FACE) },
                    modifier = Modifier.weight(1f)
                )

                AirModeButton(
                    icon = Icons.Default.ArrowDownward,
                    label = "脚部",
                    selected = airMode == AirMode.FEET,
                    onClick = { onModeChanged(AirMode.FEET) },
                    modifier = Modifier.weight(1f)
                )

                AirModeButton(
                    icon = Icons.Default.ArrowUpward,
                    label = "除雾",
                    selected = airMode == AirMode.DEFROST,
                    onClick = { onModeChanged(AirMode.DEFROST) },
                    modifier = Modifier.weight(1f)
                )

                AirModeButton(
                    icon = Icons.Default.AutoMode,
                    label = "自动",
                    selected = airMode == AirMode.AUTO,
                    onClick = { onModeChanged(AirMode.AUTO) },
                    modifier = Modifier.weight(1f)
                )
            }
        }
    }
}

@Composable
private fun AirModeButton(
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    label: String,
    selected: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Surface(
        modifier = modifier
            .aspectRatio(1f)
            .clickable(onClick = onClick),
        shape = RoundedCornerShape(12.dp),
        color = if (selected) {
            MaterialTheme.colorScheme.primary
        } else {
            MaterialTheme.colorScheme.surface
        }
    ) {
        Column(
            modifier = Modifier.fillMaxSize(),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Icon(
                icon,
                contentDescription = label,
                modifier = Modifier.size(32.dp),
                tint = if (selected) {
                    MaterialTheme.colorScheme.onPrimary
                } else {
                    MaterialTheme.colorScheme.onSurface
                }
            )
            Text(
                text = label,
                fontSize = 12.sp,
                color = if (selected) {
                    MaterialTheme.colorScheme.onPrimary
                } else {
                    MaterialTheme.colorScheme.onSurface
                }
            )
        }
    }
}

@Composable
private fun QuickControlsSection(
    isACEnabled: Boolean,
    isRecirculationEnabled: Boolean,
    isSeatVentilationEnabled: Boolean,
    isHeatedSteeringEnabled: Boolean,
    isRearDefrosterEnabled: Boolean,
    onACToggle: (Boolean) -> Unit,
    onRecirculationToggle: (Boolean) -> Unit,
    onSeatVentilationToggle: () -> Unit,
    onHeatedSteeringToggle: () -> Unit,
    onRearDefrosterToggle: () -> Unit
) {
    Surface(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        color = MaterialTheme.colorScheme.surfaceVariant
    ) {
        Column(
            modifier = Modifier.padding(20.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Text(
                text = "快速控制",
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold
            )

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceEvenly
            ) {
                QuickControlItem(
                    icon = Icons.Default.Air,
                    label = "A/C",
                    isActive = isACEnabled,
                    onClick = { onACToggle(!isACEnabled) }
                )

                QuickControlItem(
                    icon = Icons.Default.Sync,
                    label = "循环",
                    isActive = isRecirculationEnabled,
                    onClick = { onRecirculationToggle(!isRecirculationEnabled) }
                )

                QuickControlItem(
                    icon = Icons.Default.Chair,
                    label = "座椅",
                    isActive = isSeatVentilationEnabled,
                    onClick = onSeatVentilationToggle
                )

                QuickControlItem(
                    icon = Icons.Default.Adb,
                    label = "加热",
                    isActive = isHeatedSteeringEnabled,
                    onClick = onHeatedSteeringToggle
                )

                QuickControlItem(
                    icon = Icons.Default.AcUnit,
                    label = "除雾",
                    isActive = isRearDefrosterEnabled,
                    onClick = onRearDefrosterToggle
                )
            }
        }
    }
}

@Composable
private fun QuickControlItem(
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    label: String,
    isActive: Boolean,
    onClick: () -> Unit
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        Surface(
            modifier = Modifier.size(56.dp),
            shape = RoundedCornerShape(12.dp),
            color = if (isActive) {
                MaterialTheme.colorScheme.primary
            } else {
                MaterialTheme.colorScheme.surface
            },
            onClick = onClick
        ) {
            Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    icon,
                    contentDescription = label,
                    modifier = Modifier.size(28.dp),
                    tint = if (isActive) {
                        MaterialTheme.colorScheme.onPrimary
                    } else {
                        MaterialTheme.colorScheme.onSurface
                    }
                )
            }
        }

        Text(
            text = label,
            fontSize = 12.sp
        )
    }
}
