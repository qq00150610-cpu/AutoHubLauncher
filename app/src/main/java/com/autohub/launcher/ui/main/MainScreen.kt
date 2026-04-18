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
    
    val configuration = LocalConfiguration.current
    val isLandscape = configuration.screenWidthDp > configuration.screenHeightDp

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(
                Brush.verticalGradient(
                    colors = listOf(HiCarGradientStart, HiCarGradientEnd)
                )
            )
    ) {
        if (isLandscape) {
            LandscapeLayout(viewModel, uiState, currentTime, onNavigateToProfile)
        } else {
            PortraitLayout(viewModel, uiState, currentTime, onNavigateToProfile)
        }
    }
}

@Composable
private fun LandscapeLayout(
    viewModel: MainViewModel,
    uiState: MainUiState,
    currentTime: String,
    onNavigateToProfile: () -> Unit
) {
    Column(modifier = Modifier.fillMaxSize()) {
        LandscapeStatusBar(currentTime, uiState.weather) { viewModel.openSettings() }

        Row(
            modifier = Modifier
                .weight(1f)
                .fillMaxWidth()
                .padding(16.dp),
            horizontalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            LandscapeNavCard(
                modifier = Modifier.weight(0.45f),
                onClick = { viewModel.openNavigationApp() }
            )

            Column(
                modifier = Modifier.weight(0.55f),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                LandscapeMusicCard { viewModel.openMusicApp() }
                LandscapeAppGrid(uiState.installedApps) { viewModel.onAppClicked(it) }
            }
        }

        DockBar(uiState.selectedTab) { viewModel.onBottomNavTabSelected(it) }
    }
}

@Composable
private fun PortraitLayout(
    viewModel: MainViewModel,
    uiState: MainUiState,
    currentTime: String,
    onNavigateToProfile: () -> Unit
) {
    Column(modifier = Modifier.fillMaxSize()) {
        PortraitStatusBar(currentTime, uiState.weather) { viewModel.openSettings() }

        Column(
            modifier = Modifier
                .weight(1f)
                .fillMaxWidth()
                .padding(horizontal = 16.dp)
                .verticalScroll(rememberScrollState()),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Spacer(modifier = Modifier.height(8.dp))
            PortraitNavCard { viewModel.openNavigationApp() }
            PortraitMusicCard { viewModel.openMusicApp() }
            Text("快捷应用", color = HiCarTextSecondary, fontSize = 14.sp, fontWeight = FontWeight.Medium)
            PortraitAppGrid(uiState.installedApps) { viewModel.onAppClicked(it) }
            Spacer(modifier = Modifier.height(8.dp))
        }

        DockBar(uiState.selectedTab) { viewModel.onBottomNavTabSelected(it) }
    }
}

@Composable
private fun LandscapeStatusBar(time: String, weather: WeatherInfo?, onSettingsClick: () -> Unit) {
    val today = LocalDate.now()
    val dateFormatter = DateTimeFormatter.ofPattern("MM/dd", Locale.getDefault())

    Row(
        modifier = Modifier.fillMaxWidth().padding(horizontal = 20.dp, vertical = 10.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(12.dp)) {
            Text(time, color = HiCarTextPrimary, fontSize = 28.sp, fontWeight = FontWeight.Bold)
            Text(today.format(dateFormatter), color = HiCarTextSecondary, fontSize = 14.sp)
        }
        Row(horizontalArrangement = Arrangement.spacedBy(16.dp), verticalAlignment = Alignment.CenterVertically) {
            Icon(Icons.Default.WbSunny, null, tint = HiCarWarning, modifier = Modifier.size(22.dp))
            Text(weather?.let { "${it.temperature}°" } ?: "25°", color = HiCarTextPrimary, fontSize = 16.sp)
            IconButton(onClick = onSettingsClick) {
                Icon(Icons.Default.Settings, "设置", tint = HiCarTextPrimary, modifier = Modifier.size(26.dp))
            }
        }
    }
}

@Composable
private fun LandscapeNavCard(modifier: Modifier = Modifier, onClick: () -> Unit) {
    Card(
        modifier = modifier.fillMaxHeight().clip(RoundedCornerShape(24.dp)).clickable(onClick = onClick),
        shape = RoundedCornerShape(24.dp),
        colors = CardDefaults.cardColors(containerColor = Color.Transparent)
    ) {
        Box(modifier = Modifier.fillMaxSize().background(Brush.linearGradient(HiCarNavCardGradient))) {
            Column(
                modifier = Modifier.fillMaxSize().padding(24.dp),
                verticalArrangement = Arrangement.SpaceBetween
            ) {
                Column {
                    Text("智能导航", color = HiCarTextPrimary, fontSize = 26.sp, fontWeight = FontWeight.Bold)
                    Text("点击开始导航", color = HiCarTextSecondary, fontSize = 14.sp)
                }
                Box(modifier = Modifier.fillMaxWidth().weight(1f), contentAlignment = Alignment.Center) {
                    Box(
                        modifier = Modifier.size(100.dp).clip(CircleShape).background(HiCarAccentOrange.copy(alpha = 0.2f)),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(Icons.Default.Navigation, null, tint = HiCarAccentOrange, modifier = Modifier.size(56.dp))
                    }
                }
                Column {
                    Text("快捷目的地", color = HiCarTextSecondary, fontSize = 12.sp, modifier = Modifier.padding(bottom = 8.dp))
                    Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                        DestinationChip("回家", Icons.Default.Home)
                        DestinationChip("公司", Icons.Default.Work)
                    }
                }
            }
            Box(modifier = Modifier.fillMaxWidth().height(4.dp).align(Alignment.BottomCenter).background(HiCarAccentOrange))
        }
    }
}

@Composable
private fun DestinationChip(text: String, icon: ImageVector) {
    Surface(
        modifier = Modifier.clip(RoundedCornerShape(20.dp)),
        color = HiCarTextPrimary.copy(alpha = 0.15f),
        shape = RoundedCornerShape(20.dp)
    ) {
        Row(modifier = Modifier.padding(horizontal = 14.dp, vertical = 8.dp), verticalAlignment = Alignment.CenterVertically) {
            Icon(icon, null, tint = HiCarTextPrimary, modifier = Modifier.size(16.dp))
            Spacer(Modifier.width(6.dp))
            Text(text, color = HiCarTextPrimary, fontSize = 13.sp, fontWeight = FontWeight.Medium)
        }
    }
}

@Composable
private fun LandscapeMusicCard(onClick: () -> Unit) {
    Card(
        modifier = Modifier.fillMaxWidth().height(90.dp).clip(RoundedCornerShape(20.dp)).clickable(onClick = onClick),
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(containerColor = Color.Transparent)
    ) {
        Box(modifier = Modifier.fillMaxSize().background(Brush.linearGradient(HiCarMusicCardGradient))) {
            Row(modifier = Modifier.fillMaxSize().padding(16.dp), verticalAlignment = Alignment.CenterVertically) {
                Box(
                    modifier = Modifier.size(60.dp).clip(RoundedCornerShape(12.dp)).background(Color.White.copy(alpha = 0.1f)),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(Icons.Default.MusicNote, null, tint = HiCarPrimaryLight, modifier = Modifier.size(32.dp))
                }
                Spacer(Modifier.width(16.dp))
                Column(Modifier.weight(1f)) {
                    Text("正在播放", color = HiCarTextSecondary, fontSize = 12.sp)
                    Text("点击播放音乐", color = HiCarTextPrimary, fontSize = 16.sp, fontWeight = FontWeight.Bold)
                }
                IconButton(onClick, modifier = Modifier.size(44.dp).clip(CircleShape).background(HiCarTextPrimary.copy(alpha = 0.2f))) {
                    Icon(Icons.Default.PlayArrow, "播放", tint = HiCarTextPrimary, modifier = Modifier.size(26.dp))
                }
            }
        }
    }
}

@Composable
private fun LandscapeAppGrid(apps: List<AppInfo>, onAppClick: (AppInfo) -> Unit) {
    val rows = apps.take(6).chunked(3)
    Column(modifier = Modifier.fillMaxWidth().weight(1f), verticalArrangement = Arrangement.spacedBy(8.dp)) {
        rows.forEach { rowApps ->
            Row(modifier = Modifier.fillMaxWidth().weight(1f), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                rowApps.forEach { app ->
                    AppItem(app, true, Modifier.weight(1f)) { onAppClick(app) }
                }
                repeat(3 - rowApps.size) { Spacer(Modifier.weight(1f)) }
            }
        }
    }
}

@Composable
private fun PortraitStatusBar(time: String, weather: WeatherInfo?, onSettingsClick: () -> Unit) {
    val today = LocalDate.now()
    Row(
        modifier = Modifier.fillMaxWidth().padding(horizontal = 20.dp, vertical = 16.dp),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Column {
            Text(time, color = HiCarTextPrimary, fontSize = 32.sp, fontWeight = FontWeight.Bold)
            Text(today.format(DateTimeFormatter.ofPattern("MM/dd", Locale.getDefault())), color = HiCarTextSecondary, fontSize = 14.sp)
        }
        Row(verticalAlignment = Alignment.CenterVertically) {
            Icon(Icons.Default.WbSunny, null, tint = HiCarWarning, modifier = Modifier.size(24.dp))
            Spacer(Modifier.width(4.dp))
            Text(weather?.let { "${it.temperature}°" } ?: "25°", color = HiCarTextPrimary, fontSize = 18.sp)
            IconButton(onSettingsClick) {
                Icon(Icons.Default.Settings, "设置", tint = HiCarTextPrimary, modifier = Modifier.size(28.dp))
            }
        }
    }
}

@Composable
private fun PortraitNavCard(onClick: () -> Unit) {
    Card(
        modifier = Modifier.fillMaxWidth().height(140.dp).clip(RoundedCornerShape(24.dp)).clickable(onClick = onClick),
        shape = RoundedCornerShape(24.dp),
        colors = CardDefaults.cardColors(containerColor = Color.Transparent)
    ) {
        Box(modifier = Modifier.fillMaxSize().background(Brush.linearGradient(HiCarNavCardGradient))) {
            Row(modifier = Modifier.fillMaxSize().padding(24.dp), verticalAlignment = Alignment.CenterVertically) {
                Box(
                    modifier = Modifier.size(64.dp).clip(CircleShape).background(HiCarAccentOrange.copy(alpha = 0.2f)),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(Icons.Default.Navigation, null, tint = HiCarAccentOrange, modifier = Modifier.size(36.dp))
                }
                Spacer(Modifier.width(20.dp))
                Column {
                    Text("导航", color = HiCarTextPrimary, fontSize = 22.sp, fontWeight = FontWeight.Bold)
                    Text("点击开始导航", color = HiCarTextSecondary, fontSize = 14.sp)
                }
            }
            Box(modifier = Modifier.fillMaxWidth().height(4.dp).align(Alignment.BottomCenter).background(HiCarAccentOrange))
        }
    }
}

@Composable
private fun PortraitMusicCard(onClick: () -> Unit) {
    Card(
        modifier = Modifier.fillMaxWidth().height(100.dp).clip(RoundedCornerShape(20.dp)).clickable(onClick = onClick),
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(containerColor = Color.Transparent)
    ) {
        Box(modifier = Modifier.fillMaxSize().background(Brush.linearGradient(HiCarMusicCardGradient))) {
            Row(modifier = Modifier.fillMaxSize().padding(16.dp), verticalAlignment = Alignment.CenterVertically) {
                Box(
                    modifier = Modifier.size(64.dp).clip(RoundedCornerShape(12.dp)).background(Color.White.copy(alpha = 0.1f)),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(Icons.Default.MusicNote, null, tint = HiCarPrimaryLight, modifier = Modifier.size(32.dp))
                }
                Spacer(Modifier.width(16.dp))
                Column(Modifier.weight(1f)) {
                    Text("正在播放", color = HiCarTextSecondary, fontSize = 12.sp)
                    Text("点击播放音乐", color = HiCarTextPrimary, fontSize = 16.sp, fontWeight = FontWeight.Bold)
                }
                IconButton(onClick, modifier = Modifier.size(44.dp).clip(CircleShape).background(HiCarTextPrimary.copy(alpha = 0.2f))) {
                    Icon(Icons.Default.PlayArrow, "播放", tint = HiCarTextPrimary, modifier = Modifier.size(26.dp))
                }
            }
        }
    }
}

@Composable
private fun PortraitAppGrid(apps: List<AppInfo>, onAppClick: (AppInfo) -> Unit) {
    val rows = apps.chunked(4)
    Column(modifier = Modifier.fillMaxWidth(), verticalArrangement = Arrangement.spacedBy(12.dp)) {
        rows.forEach { rowApps ->
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                rowApps.forEach { app -> AppItem(app, false, Modifier.weight(1f)) { onAppClick(app) } }
                repeat(4 - rowApps.size) { Spacer(Modifier.weight(1f)) }
            }
        }
    }
}

@Composable
private fun AppItem(app: AppInfo, isLandscape: Boolean, modifier: Modifier = Modifier, onClick: () -> Unit) {
    Column(
        modifier = modifier
            .clip(RoundedCornerShape(if (isLandscape) 12.dp else 16.dp))
            .background(HiCarGlassBackground)
            .clickable(onClick = onClick)
            .padding(if (isLandscape) 8.dp else 12.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Box(
            modifier = Modifier.size(if (isLandscape) 44.dp else 48.dp)
                .clip(RoundedCornerShape(if (isLandscape) 12.dp else 14.dp))
                .background(Brush.linearGradient(listOf(HiCarPrimary, HiCarPrimaryDark))),
            contentAlignment = Alignment.Center
        ) {
            Text(app.name.take(1), color = Color.White, fontSize = if (isLandscape) 18.sp else 20.sp, fontWeight = FontWeight.Bold)
        }
        Spacer(Modifier.height(if (isLandscape) 4.dp else 8.dp))
        Text(app.name, color = HiCarTextPrimary, fontSize = if (isLandscape) 11.sp else 12.sp, maxLines = 1, overflow = TextOverflow.Ellipsis)
    }
}

@Composable
private fun DockBar(selectedTab: BottomNavTab, onTabSelected: (BottomNavTab) -> Unit) {
    Surface(
        modifier = Modifier.fillMaxWidth(),
        color = HiCarDockBackground,
        shape = RoundedCornerShape(topStart = 24.dp, topEnd = 24.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp, vertical = 12.dp),
            horizontalArrangement = Arrangement.SpaceEvenly
        ) {
            DockItem(Icons.Outlined.Home, Icons.Filled.Home, "首页", selectedTab == BottomNavTab.Home, HiCarPrimary) { onTabSelected(BottomNavTab.Home) }
            DockItem(Icons.Outlined.Navigation, Icons.Filled.Navigation, "导航", selectedTab == BottomNavTab.Navigation, HiCarAccentOrange) { onTabSelected(BottomNavTab.Navigation) }
            DockItem(Icons.Outlined.MusicNote, Icons.Filled.MusicNote, "音乐", selectedTab == BottomNavTab.Music, HiCarPrimaryLight) { onTabSelected(BottomNavTab.Music) }
            DockItem(Icons.Outlined.VideoLibrary, Icons.Filled.VideoLibrary, "视频", selectedTab == BottomNavTab.Video, HiCarPrimary) { onTabSelected(BottomNavTab.Video) }
            DockItem(Icons.Outlined.Settings, Icons.Filled.Settings, "设置", selectedTab == BottomNavTab.Settings, HiCarPrimary) { onTabSelected(BottomNavTab.Settings) }
        }
    }
}

@Composable
private fun DockItem(icon: ImageVector, activeIcon: ImageVector, label: String, isSelected: Boolean, accentColor: Color, onClick: () -> Unit) {
    val color by animateColorAsState(if (isSelected) accentColor else HiCarDockItemInactive)
    Column(
        modifier = Modifier.clip(RoundedCornerShape(16.dp)).clickable(onClick = onClick).padding(horizontal = 16.dp, vertical = 8.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Icon(if (isSelected) activeIcon else icon, label, tint = color, modifier = Modifier.size(28.dp))
        Spacer(Modifier.height(4.dp))
        Text(label, color = color, fontSize = 11.sp, fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal)
    }
}
