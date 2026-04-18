package com.autohub.launcher.ui.main

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.autohub.launcher.domain.model.AppInfo
import com.autohub.launcher.domain.model.WeatherInfo
import com.autohub.launcher.ui.theme.*
import java.time.LocalDate
import java.time.format.DateTimeFormatter

@Composable
fun MainScreen(
    viewModel: MainViewModel,
    onNavigateToSettings: () -> Unit,
    onNavigateToNavigation: () -> Unit,
    onNavigateToMusic: () -> Unit,
    onNavigateToVideo: () -> Unit,
    onNavigateToProfile: () -> Unit = {}
) {
    val uiState by viewModel.uiState.collectAsState()
    val currentTime by viewModel.currentTime.collectAsState()
    val screenDims = rememberScreenDimensions()

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(
                Brush.verticalGradient(
                    colors = listOf(HiCarGradientStart, HiCarGradientEnd)
                )
            )
    ) {
        Column(
            modifier = Modifier.fillMaxSize()
        ) {
            // 顶部状态栏
            HiCarStatusBar(
                time = currentTime,
                weather = uiState.weather,
                onSettingsClick = { viewModel.openSettings() },
                onProfileClick = onNavigateToProfile
            )

            // 主内容区域
            Column(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                Spacer(modifier = Modifier.height(8.dp))
                
                // 导航卡片（大卡片）
                HiCarNavigationCard(
                    onClick = { viewModel.openNavigationApp() }
                )

                // 音乐卡片
                HiCarMusicCard(
                    onClick = { viewModel.openMusicApp() }
                )

                // 快捷应用标题
                Text(
                    text = "快捷应用",
                    color = HiCarTextSecondary,
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Medium,
                    modifier = Modifier.padding(start = 4.dp)
                )

                // 快捷应用网格
                LazyVerticalGrid(
                    columns = GridCells.Fixed(4),
                    horizontalArrangement = Arrangement.spacedBy(12.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp),
                    modifier = Modifier.weight(1f)
                ) {
                    items(uiState.installedApps) { app ->
                        HiCarAppGridItem(
                            app = app,
                            onClick = { viewModel.onAppClicked(app) }
                        )
                    }
                }

                Spacer(modifier = Modifier.height(8.dp))
            }

            // 底部 DOCK 栏
            HiCarDockBar(
                selectedTab = uiState.selectedTab,
                onTabSelected = { tab -> viewModel.onBottomNavTabSelected(tab) }
            )
        }
    }
}

@Composable
private fun HiCarStatusBar(
    time: String,
    weather: WeatherInfo?,
    onSettingsClick: () -> Unit,
    onProfileClick: () -> Unit
) {
    val today = LocalDate.now()
    val dateFormatter = DateTimeFormatter.ofPattern("MM月dd日 E")
    val dateString = today.format(dateFormatter)

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 20.dp, vertical = 16.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        // 左侧 - 时间日期
        Column {
            Text(
                text = time,
                color = HiCarTextPrimary,
                fontSize = 32.sp,
                fontWeight = FontWeight.Bold
            )
            Text(
                text = dateString,
                color = HiCarTextSecondary,
                fontSize = 14.sp
            )
        }

        // 右侧 - 天气和设置
        Row(
            horizontalArrangement = Arrangement.spacedBy(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // 天气信息
            weather?.let {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.WbSunny,
                        contentDescription = null,
                        tint = HiCarWarning,
                        modifier = Modifier.size(24.dp)
                    )
                    Text(
                        text = "${it.temperature}°",
                        color = HiCarTextPrimary,
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Medium
                    )
                }
            } ?: run {
                // 默认天气图标
                Icon(
                    imageVector = Icons.Default.WbSunny,
                    contentDescription = null,
                    tint = HiCarWarning,
                    modifier = Modifier.size(24.dp)
                )
                Text(
                    text = "25°",
                    color = HiCarTextPrimary,
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Medium
                )
            }

            // 设置按钮
            IconButton(onClick = onSettingsClick) {
                Icon(
                    imageVector = Icons.Default.Settings,
                    contentDescription = "设置",
                    tint = HiCarTextPrimary,
                    modifier = Modifier.size(28.dp)
                )
            }
        }
    }
}

@Composable
private fun HiCarNavigationCard(
    onClick: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .height(160.dp)
            .clip(RoundedCornerShape(24.dp))
            .clickable(onClick = onClick),
        shape = RoundedCornerShape(24.dp),
        colors = CardDefaults.cardColors(
            containerColor = Color.Transparent
        )
    ) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(
                    Brush.linearGradient(HiCarNavCardGradient)
                )
        ) {
            // 背景装饰 - 道路图形
            Icon(
                imageVector = Icons.Default.NearMe,
                contentDescription = null,
                tint = Color.White.copy(alpha = 0.1f),
                modifier = Modifier
                    .size(200.dp)
                    .align(Alignment.CenterEnd)
                    .offset(x = 40.dp)
            )

            // 内容
            Row(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(24.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                // 左侧图标
                Box(
                    modifier = Modifier
                        .size(72.dp)
                        .clip(CircleShape)
                        .background(HiCarAccentOrange.copy(alpha = 0.2f)),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Default.Navigation,
                        contentDescription = null,
                        tint = HiCarAccentOrange,
                        modifier = Modifier.size(40.dp)
                    )
                }

                Spacer(modifier = Modifier.width(20.dp))

                // 文字信息
                Column {
                    Text(
                        text = "导航",
                        color = HiCarTextPrimary,
                        fontSize = 24.sp,
                        fontWeight = FontWeight.Bold
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = "点击开始导航",
                        color = HiCarTextSecondary,
                        fontSize = 14.sp
                    )
                    Spacer(modifier = Modifier.height(12.dp))
                    // 快捷目的地按钮
                    Row(
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        QuickDestinationChip("回家")
                        QuickDestinationChip("公司")
                    }
                }
            }

            // 底部装饰线
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(4.dp)
                    .align(Alignment.BottomCenter)
                    .background(HiCarAccentOrange)
            )
        }
    }
}

@Composable
private fun QuickDestinationChip(text: String) {
    Surface(
        modifier = Modifier.clip(RoundedCornerShape(16.dp)),
        color = HiCarTextPrimary.copy(alpha = 0.1f),
        shape = RoundedCornerShape(16.dp)
    ) {
        Text(
            text = text,
            color = HiCarTextPrimary,
            fontSize = 12.sp,
            modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp)
        )
    }
}

@Composable
private fun HiCarMusicCard(
    onClick: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .height(120.dp)
            .clip(RoundedCornerShape(20.dp))
            .clickable(onClick = onClick),
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(
            containerColor = Color.Transparent
        )
    ) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(
                    Brush.linearGradient(HiCarMusicCardGradient)
                )
        ) {
            Row(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(20.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                // 专辑封面占位
                Box(
                    modifier = Modifier
                        .size(80.dp)
                        .clip(RoundedCornerShape(12.dp))
                        .background(Color.White.copy(alpha = 0.1f)),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Default.MusicNote,
                        contentDescription = null,
                        tint = HiCarPrimaryLight,
                        modifier = Modifier.size(40.dp)
                    )
                }

                Spacer(modifier = Modifier.width(16.dp))

                // 歌曲信息
                Column(
                    modifier = Modifier.weight(1f)
                ) {
                    Text(
                        text = "正在播放",
                        color = HiCarTextSecondary,
                        fontSize = 12.sp
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = "点击播放音乐",
                        color = HiCarTextPrimary,
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                }

                // 播放控制
                Row(
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    IconButton(
                        onClick = onClick,
                        modifier = Modifier
                            .size(48.dp)
                            .clip(CircleShape)
                            .background(HiCarTextPrimary.copy(alpha = 0.2f))
                    ) {
                        Icon(
                            imageVector = Icons.Default.PlayArrow,
                            contentDescription = "播放",
                            tint = HiCarTextPrimary,
                            modifier = Modifier.size(28.dp)
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun HiCarAppGridItem(
    app: AppInfo,
    onClick: () -> Unit
) {
    var isPressed by remember { mutableStateOf(false) }
    val scale by animateFloatAsState(
        targetValue = if (isPressed) 0.92f else 1f,
        label = "scale"
    )

    Column(
        modifier = Modifier
            .scale(scale)
            .clip(RoundedCornerShape(16.dp))
            .background(HiCarGlassBackground)
            .clickable(
                onClick = {
                    isPressed = true
                    onClick()
                }
            )
            .padding(12.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        // 应用图标
        Box(
            modifier = Modifier
                .size(52.dp)
                .clip(RoundedCornerShape(14.dp))
                .background(
                    Brush.linearGradient(
                        colors = listOf(HiCarPrimary, HiCarPrimaryDark)
                    )
                ),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = app.name.take(1),
                color = Color.White,
                fontSize = 22.sp,
                fontWeight = FontWeight.Bold
            )
        }

        Spacer(modifier = Modifier.height(8.dp))

        // 应用名称
        Text(
            text = app.name,
            color = HiCarTextPrimary,
            fontSize = 12.sp,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis,
            textAlign = TextAlign.Center
        )
    }

    LaunchedEffect(isPressed) {
        if (isPressed) {
            kotlinx.coroutines.delay(100)
            isPressed = false
        }
    }
}

@Composable
private fun HiCarDockBar(
    selectedTab: BottomNavTab,
    onTabSelected: (BottomNavTab) -> Unit
) {
    Surface(
        modifier = Modifier.fillMaxWidth(),
        color = HiCarDockBackground,
        shape = RoundedCornerShape(topStart = 24.dp, topEnd = 24.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 12.dp),
            horizontalArrangement = Arrangement.SpaceEvenly,
            verticalAlignment = Alignment.CenterVertically
        ) {
            HiCarDockItem(
                icon = Icons.Outlined.Home,
                activeIcon = Icons.Filled.Home,
                label = "首页",
                isSelected = selectedTab == BottomNavTab.Home,
                onClick = { onTabSelected(BottomNavTab.Home) }
            )

            HiCarDockItem(
                icon = Icons.Outlined.Navigation,
                activeIcon = Icons.Filled.Navigation,
                label = "导航",
                isSelected = selectedTab == BottomNavTab.Navigation,
                onClick = { onTabSelected(BottomNavTab.Navigation) },
                accentColor = HiCarAccentOrange
            )

            HiCarDockItem(
                icon = Icons.Outlined.MusicNote,
                activeIcon = Icons.Filled.MusicNote,
                label = "音乐",
                isSelected = selectedTab == BottomNavTab.Music,
                onClick = { onTabSelected(BottomNavTab.Music) },
                accentColor = HiCarPrimaryLight
            )

            HiCarDockItem(
                icon = Icons.Outlined.VideoLibrary,
                activeIcon = Icons.Filled.VideoLibrary,
                label = "视频",
                isSelected = selectedTab == BottomNavTab.Video,
                onClick = { onTabSelected(BottomNavTab.Video) }
            )

            HiCarDockItem(
                icon = Icons.Outlined.Settings,
                activeIcon = Icons.Filled.Settings,
                label = "设置",
                isSelected = selectedTab == BottomNavTab.Settings,
                onClick = { onTabSelected(BottomNavTab.Settings) }
            )
        }
    }
}

@Composable
private fun HiCarDockItem(
    icon: ImageVector,
    activeIcon: ImageVector,
    label: String,
    isSelected: Boolean,
    onClick: () -> Unit,
    accentColor: Color = HiCarPrimary
) {
    val iconColor by animateColorAsState(
        targetValue = if (isSelected) accentColor else HiCarDockItemInactive,
        label = "iconColor"
    )
    val labelColor by animateColorAsState(
        targetValue = if (isSelected) accentColor else HiCarDockItemInactive,
        label = "labelColor"
    )

    Column(
        modifier = Modifier
            .clip(RoundedCornerShape(16.dp))
            .clickable(onClick = onClick)
            .padding(horizontal = 16.dp, vertical = 8.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Icon(
            imageVector = if (isSelected) activeIcon else icon,
            contentDescription = label,
            tint = iconColor,
            modifier = Modifier.size(28.dp)
        )
        Spacer(modifier = Modifier.height(4.dp))
        Text(
            text = label,
            color = labelColor,
            fontSize = 11.sp,
            fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal
        )
    }
}

// Extension function for offset
private fun Modifier.offset(x: androidx.compose.ui.unit.Dp): Modifier = this.then(
    Modifier.padding(end = x)
)
