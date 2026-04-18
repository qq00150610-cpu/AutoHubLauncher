package com.autohub.launcher.ui.main

import androidx.compose.animation.animateColorAsState
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalConfiguration
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
import java.util.Locale

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
    val config = LocalConfiguration.current
    val isLandscape = config.screenWidthDp > config.screenHeightDp

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Brush.verticalGradient(listOf(BgGradientStart, BgGradientEnd)))
    ) {
        if (isLandscape) {
            LandscapeUI(viewModel, uiState, currentTime)
        } else {
            PortraitUI(viewModel, uiState, currentTime)
        }
    }
}

// 横屏布局
@Composable
private fun LandscapeUI(
    viewModel: MainViewModel,
    uiState: MainUiState,
    currentTime: String
) {
    Column(modifier = Modifier.fillMaxSize()) {
        // 顶部状态栏
        TopBar(currentTime, uiState.weather) { viewModel.openSettings() }
        
        // 主内容
        Row(
            modifier = Modifier.weight(1f).fillMaxWidth().padding(12.dp),
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            // 左侧地图卡片
            MapCard(Modifier.weight(0.5f)) { viewModel.openNavigationApp() }
            
            // 右侧
            Column(modifier = Modifier.weight(0.5f), verticalArrangement = Arrangement.spacedBy(12.dp)) {
                MediaCard { viewModel.openMusicApp() }
                AppGrid(uiState.installedApps.take(6), 3) { viewModel.onAppClicked(it) }
            }
        }
        
        // 底部栏
        BottomBar(uiState.selectedTab) { viewModel.onBottomNavTabSelected(it) }
    }
}

// 竖屏布局
@Composable
private fun PortraitUI(
    viewModel: MainViewModel,
    uiState: MainUiState,
    currentTime: String
) {
    Column(modifier = Modifier.fillMaxSize()) {
        TopBar(currentTime, uiState.weather) { viewModel.openSettings() }
        
        Column(
            modifier = Modifier
                .weight(1f)
                .fillMaxWidth()
                .padding(12.dp)
                .verticalScroll(rememberScrollState()),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            MapCard(Modifier.fillMaxWidth().height(160.dp)) { viewModel.openNavigationApp() }
            MediaCard { viewModel.openMusicApp() }
            Text("快捷应用", color = TextSecondary, fontSize = 14.sp, fontWeight = FontWeight.Medium)
            AppGrid(uiState.installedApps.take(8), 4) { viewModel.onAppClicked(it) }
        }
        
        BottomBar(uiState.selectedTab) { viewModel.onBottomNavTabSelected(it) }
    }
}

// 顶部状态栏
@Composable
private fun TopBar(time: String, weather: WeatherInfo?, onSettings: () -> Unit) {
    val date = LocalDate.now().format(DateTimeFormatter.ofPattern("MM/dd", Locale.getDefault()))
    Row(
        modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp, vertical = 8.dp),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Text(time, color = TextPrimary, fontSize = 24.sp, fontWeight = FontWeight.Bold)
            Spacer(Modifier.width(8.dp))
            Text(date, color = TextSecondary, fontSize = 14.sp)
        }
        Row(verticalAlignment = Alignment.CenterVertically) {
            Icon(Icons.Default.WbSunny, null, tint = Color(0xFFFFC107), modifier = Modifier.size(20.dp))
            Text(" ${weather?.temperature ?: 25}°", color = TextPrimary, fontSize = 14.sp)
            IconButton(onSettings) { Icon(Icons.Default.Settings, null, tint = TextPrimary) }
        }
    }
}

// 地图卡片
@Composable
private fun MapCard(modifier: Modifier = Modifier, onClick: () -> Unit) {
    Card(
        modifier = modifier.clip(RoundedCornerShape(20.dp)).clickable(onClick = onClick),
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(containerColor = Color(0xDD1a1a2e))
    ) {
        Box(modifier = Modifier.fillMaxSize()) {
            // 背景
            Box(
                modifier = Modifier.fillMaxSize()
                    .background(Brush.linearGradient(listOf(Color(0xFF1E4D78), Color(0xFF0D2840))))
            )
            
            // 内容
            Row(
                modifier = Modifier.fillMaxSize().padding(16.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                // 图标
                Box(
                    modifier = Modifier.size(56.dp).clip(CircleShape).background(Color(0x33FF6D00)),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(Icons.Default.Navigation, null, tint = Color(0xFFFF6D00), modifier = Modifier.size(32.dp))
                }
                
                Spacer(Modifier.width(16.dp))
                
                // 文字
                Column {
                    Text("导航", color = TextPrimary, fontSize = 20.sp, fontWeight = FontWeight.Bold)
                    Text("点击开始导航", color = TextSecondary, fontSize = 12.sp)
                    Spacer(Modifier.height(8.dp))
                    Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        SmallChip("回家")
                        SmallChip("公司")
                    }
                }
            }
            
            // 底部装饰
            Box(
                modifier = Modifier.fillMaxWidth().height(3.dp).align(Alignment.BottomCenter)
                    .background(Color(0xFFFF6D00))
            )
        }
    }
}

@Composable
private fun SmallChip(text: String) {
    Surface(
        modifier = Modifier.clip(RoundedCornerShape(12.dp)),
        color = Color(0x20FFFFFF),
        shape = RoundedCornerShape(12.dp)
    ) {
        Text(text, color = TextPrimary, fontSize = 11.sp, modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp))
    }
}

// 媒体卡片
@Composable
private fun MediaCard(onClick: () -> Unit) {
    Card(
        modifier = Modifier.fillMaxWidth().height(80.dp).clip(RoundedCornerShape(16.dp)).clickable(onClick = onClick),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = Color(0xDD2D1B55))
    ) {
        Row(
            modifier = Modifier.fillMaxSize().padding(12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier.size(48.dp).clip(RoundedCornerShape(10.dp)).background(Color(0x20FFFFFF)),
                contentAlignment = Alignment.Center
            ) {
                Icon(Icons.Default.MusicNote, null, tint = AccentGreen, modifier = Modifier.size(28.dp))
            }
            Spacer(Modifier.width(12.dp))
            Column(Modifier.weight(1f)) {
                Text("正在播放", color = TextSecondary, fontSize = 11.sp)
                Text("点击播放音乐", color = TextPrimary, fontSize = 14.sp, fontWeight = FontWeight.Bold)
            }
            Box(
                modifier = Modifier.size(40.dp).clip(CircleShape).background(Color(0x30FFFFFF)),
                contentAlignment = Alignment.Center
            ) {
                Icon(Icons.Default.PlayArrow, null, tint = TextPrimary, modifier = Modifier.size(24.dp))
            }
        }
    }
}

// 应用网格
@Composable
private fun AppGrid(apps: List<AppInfo>, columns: Int, onClick: (AppInfo) -> Unit) {
    val rows = apps.chunked(columns)
    Column(modifier = Modifier.fillMaxWidth(), verticalArrangement = Arrangement.spacedBy(8.dp)) {
        rows.forEach { row ->
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                row.forEach { app ->
                    AppItem(Modifier.weight(1f), app) { onClick(app) }
                }
                repeat(columns - row.size) { Spacer(Modifier.weight(1f)) }
            }
        }
    }
}

@Composable
private fun AppItem(modifier: Modifier, app: AppInfo, onClick: () -> Unit) {
    Column(
        modifier = modifier.clip(RoundedCornerShape(12.dp)).background(Color(0x20FFFFFF)).clickable(onClick = onClick).padding(8.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Box(
            modifier = Modifier.size(40.dp).clip(RoundedCornerShape(10.dp))
                .background(Brush.linearGradient(listOf(Color(0xFF2196F3), Color(0xFF1976D2)))),
            contentAlignment = Alignment.Center
        ) {
            Text(app.name.take(1), color = Color.White, fontSize = 16.sp, fontWeight = FontWeight.Bold)
        }
        Spacer(Modifier.height(4.dp))
        Text(app.name, color = TextPrimary, fontSize = 10.sp, maxLines = 1, overflow = TextOverflow.Ellipsis)
    }
}

// 底部栏
@Composable
private fun BottomBar(selected: BottomNavTab, onSelect: (BottomNavTab) -> Unit) {
    Surface(
        modifier = Modifier.fillMaxWidth(),
        color = Color(0xE61a1a2e),
        shape = RoundedCornerShape(topStart = 20.dp, topEnd = 20.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth().padding(vertical = 8.dp),
            horizontalArrangement = Arrangement.SpaceEvenly
        ) {
            DockBtn(Icons.Outlined.Home, Icons.Filled.Home, "首页", selected == BottomNavTab.Home) { onSelect(BottomNavTab.Home) }
            DockBtn(Icons.Outlined.Navigation, Icons.Filled.Navigation, "导航", selected == BottomNavTab.Navigation, Color(0xFFFF6D00)) { onSelect(BottomNavTab.Navigation) }
            DockBtn(Icons.Outlined.MusicNote, Icons.Filled.MusicNote, "音乐", selected == BottomNavTab.Music, AccentGreen) { onSelect(BottomNavTab.Music) }
            DockBtn(Icons.Outlined.VideoLibrary, Icons.Filled.VideoLibrary, "视频", selected == BottomNavTab.Video) { onSelect(BottomNavTab.Video) }
            DockBtn(Icons.Outlined.Settings, Icons.Filled.Settings, "设置", selected == BottomNavTab.Settings) { onSelect(BottomNavTab.Settings) }
        }
    }
}

@Composable
private fun DockBtn(icon: ImageVector, activeIcon: ImageVector, label: String, selected: Boolean, accent: Color = Color(0xFF2196F3), onClick: () -> Unit) {
    val color by animateColorAsState(if (selected) accent else Color(0x80FFFFFF))
    Column(
        modifier = Modifier.clip(RoundedCornerShape(12.dp)).clickable(onClick = onClick).padding(horizontal = 12.dp, vertical = 6.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Icon(if (selected) activeIcon else icon, label, tint = color, modifier = Modifier.size(24.dp))
        Spacer(Modifier.height(2.dp))
        Text(label, color = color, fontSize = 10.sp, fontWeight = if (selected) FontWeight.Bold else FontWeight.Normal)
    }
}
