package com.autohub.launcher.ui.main

import androidx.compose.animation.animateColorAsState
import androidx.compose.foundation.*
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
        TopStatusBar(currentTime, uiState.weather) { viewModel.openSettings() }
        
        // 主内容
        Row(
            modifier = Modifier.weight(1f).fillMaxWidth().padding(12.dp),
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            // 左侧地图卡片
            MapCardView(Modifier.weight(0.45f)) { viewModel.openNavigationApp() }
            
            // 右侧
            Column(modifier = Modifier.weight(0.55f), verticalArrangement = Arrangement.spacedBy(12.dp)) {
                MediaCardView { viewModel.openMusicApp() }
                AppGridView(uiState.installedApps.take(10), 5) { viewModel.onAppClicked(it) }
            }
        }
        
        // 底部栏
        BottomDock(uiState.selectedTab) { viewModel.onBottomNavTabSelected(it) }
    }
}

// 竖屏布局
@Composable
private fun PortraitUI(viewModel: MainViewModel, uiState: MainUiState, currentTime: String) {
    Column(modifier = Modifier.fillMaxSize()) {
        PortraitStatusBar(currentTime, uiState.weather) { viewModel.openSettings() }
        
        Column(
            modifier = Modifier.weight(1f).fillMaxWidth().verticalScroll(rememberScrollState()).padding(12.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            MapCardPortrait { viewModel.openNavigationApp() }
            MediaCardPortrait { viewModel.openMusicApp() }
            Text("快捷应用", color = TextSecondary, fontSize = 14.sp, fontWeight = FontWeight.Medium, modifier = Modifier.padding(horizontal = 4.dp))
            AppGridView(uiState.installedApps.take(10), 5) { viewModel.onAppClicked(it) }
        }
        
        SystemBar(
            onHome = { }, onNav = { viewModel.openNavigationApp() },
            onRefresh = { viewModel.onResume() }, onLock = { }, onRotate = { }, onVolume = { }
        )
    }
}

// 顶部状态栏
@Composable
private fun TopStatusBar(time: String, weather: WeatherInfo?, onSettings: () -> Unit) {
    val date = LocalDate.now().format(DateTimeFormatter.ofPattern("MM/dd", Locale.getDefault()))
    Row(
        modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp, vertical = 10.dp),
        horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Text(time, color = TextPrimary, fontSize = 28.sp, fontWeight = FontWeight.Bold)
            Spacer(Modifier.width(10.dp))
            Text(date, color = TextSecondary, fontSize = 14.sp)
        }
        Row(verticalAlignment = Alignment.CenterVertically) {
            Icon(Icons.Default.WbSunny, null, AccentOrange, Modifier.size(22.dp))
            Text(" ${weather?.temperature ?: 25}°", color = TextPrimary, fontSize = 16.sp)
            Spacer(Modifier.width(10.dp))
            Text(weather?.location ?: "昆山市", color = TextSecondary, fontSize = 14.sp)
            IconButton(onClick = onSettings) { Icon(Icons.Default.Settings, "设置", TextPrimary, Modifier.size(26.dp)) }
        }
    }
}

// 竖屏状态栏
@Composable
private fun PortraitStatusBar(time: String, weather: WeatherInfo?, onSettings: () -> Unit) {
    Row(
        modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp, vertical = 12.dp),
        horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically
    ) {
        Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(10.dp)) {
            Box(Modifier.size(32.dp).clip(CircleShape).background(AccentBlue.copy(alpha = 0.3f)), Alignment.Center) {
                Icon(Icons.Default.Person, null, Color.White, Modifier.size(18.dp))
            }
            Icon(Icons.Default.SignalCellularAlt, null, AccentGreen, Modifier.size(16.dp))
            Icon(Icons.Default.Notifications, null, TextSecondary, Modifier.size(16.dp))
        }
        Text(time, TextPrimary, 26.sp, FontWeight.Bold)
        Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            weather?.let {
                Column(horizontalAlignment = Alignment.End) {
                    Text(it.location ?: "昆山市", color = TextSecondary, fontSize = 11.sp)
                    Text("${it.temperature}°", color = TextPrimary, fontSize = 13.sp)
                }
            }
            Icon(Icons.Default.WbSunny, null, AccentOrange, Modifier.size(18.dp))
            IconButton(onClick = onSettings) { Icon(Icons.Default.Settings, "设置", TextPrimary, Modifier.size(22.dp)) }
        }
    }
}

// 地图卡片
@Composable
private fun MapCardView(modifier: Modifier = Modifier, onClick: () -> Unit) {
    Card(
        modifier = modifier.clip(RoundedCornerShape(20.dp)).clickable(onClick = onClick),
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(containerColor = Color(0xDD1a2744))
    ) {
        Box(modifier = Modifier.fillMaxSize().background(Brush.linearGradient(listOf(Color(0xFF1E4D78), Color(0xFF0D2840))))) {
            // 路网纹理
            Canvas(modifier = Modifier.fillMaxSize()) {
                val gridSize = 30.dp.toPx()
                for (y in 0..size.height.toInt() step gridSize.toInt()) {
                    drawLine(Color.White.copy(alpha = 0.04f), Offset(0f, y.toFloat()), Offset(size.width, y.toFloat()), 1.dp.toPx())
                }
                for (x in 0..size.width.toInt() step gridSize.toInt()) {
                    drawLine(Color.White.copy(alpha = 0.04f), Offset(x.toFloat(), 0f), Offset(x.toFloat(), size.height), 1.dp.toPx())
                }
            }
            
            IconButton(onClick = { }, modifier = Modifier.align(Alignment.TopEnd).padding(8.dp).size(28.dp).clip(CircleShape).background(Color.White.copy(alpha = 0.1f))) {
                Icon(Icons.Default.Close, "关闭", Color.White, Modifier.size(16.dp))
            }
            
            Row(modifier = Modifier.fillMaxSize().padding(20.dp), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
                Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                    Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                        Box(Modifier.size(44.dp).clip(RoundedCornerShape(10.dp)).background(AccentBlue.copy(alpha = 0.25f)), Alignment.Center) {
                            Icon(Icons.Default.Map, null, AccentBlue, Modifier.size(26.dp))
                        }
                        Text("高德地图", TextPrimary, 16.sp, FontWeight.Medium)
                    }
                    Text("连接真实世界", TextPrimary, 24.sp, FontWeight.Bold)
                    Text("让出行更美好", TextSecondary, 14.sp)
                    Spacer(Modifier.height(8.dp))
                    Button(
                        onClick = onClick,
                        colors = ButtonDefaults.buttonColors(containerColor = AccentBlue),
                        shape = RoundedCornerShape(20.dp),
                        contentPadding = PaddingValues(horizontal = 16.dp, vertical = 8.dp)
                    ) {
                        Icon(Icons.Default.Send, null, Modifier.size(16.dp))
                        Spacer(Modifier.width(6.dp))
                        Text("打开地图", 13.sp, FontWeight.Medium)
                    }
                }
                
                // 导航箭头
                Box(Modifier.size(80.dp), contentAlignment = Alignment.Center) {
                    Box(Modifier.size(70.dp).clip(CircleShape).background(Brush.radialGradient(listOf(AccentBlue.copy(alpha = 0.25f), AccentBlue.copy(alpha = 0f)))))
                    Canvas(Modifier.size(50.dp)) {
                        val path = androidx.compose.ui.graphics.Path().apply {
                            moveTo(size.width / 2, 0f)
                            lineTo(size.width, size.height * 0.7f)
                            lineTo(size.width / 2, size.height * 0.5f)
                            lineTo(0f, size.height * 0.7f)
                            close()
                        }
                        drawPath(path, Brush.verticalGradient(listOf(AccentBlue, AccentBlue.copy(alpha = 0.7f))))
                    }
                }
            }
        }
    }
}

// 竖屏地图卡片
@Composable
private fun MapCardPortrait(onClick: () -> Unit) {
    Card(
        modifier = Modifier.fillMaxWidth().height(180.dp).clip(RoundedCornerShape(20.dp)).clickable(onClick = onClick),
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(containerColor = Color(0xDD1a2744))
    ) {
        Box(modifier = Modifier.fillMaxSize().background(Brush.linearGradient(listOf(Color(0xFF1E4D78), Color(0xFF0D2840))))) {
            Canvas(modifier = Modifier.fillMaxSize()) {
                val gridSize = 25.dp.toPx()
                for (y in 0..size.height.toInt() step gridSize.toInt()) {
                    drawLine(Color.White.copy(alpha = 0.03f), Offset(0f, y.toFloat()), Offset(size.width, y.toFloat()), 1.dp.toPx())
                }
                for (x in 0..size.width.toInt() step gridSize.toInt()) {
                    drawLine(Color.White.copy(alpha = 0.03f), Offset(x.toFloat(), 0f), Offset(x.toFloat(), size.height), 1.dp.toPx())
                }
            }
            
            IconButton(onClick = { }, modifier = Modifier.align(Alignment.TopEnd).padding(8.dp).size(26.dp).clip(CircleShape).background(Color.White.copy(alpha = 0.1f))) {
                Icon(Icons.Default.Close, "关闭", Color.White, Modifier.size(14.dp))
            }
            
            Column(modifier = Modifier.fillMaxSize().padding(16.dp), verticalArrangement = Arrangement.SpaceBetween) {
                Box(Modifier.size(40.dp).clip(CircleShape).background(Brush.radialGradient(listOf(AccentBlue.copy(alpha = 0.25f), AccentBlue.copy(alpha = 0f)))), Alignment.Center) {
                    Canvas(Modifier.size(24.dp)) {
                        val path = androidx.compose.ui.graphics.Path().apply {
                            moveTo(size.width / 2, 0f)
                            lineTo(size.width, size.height * 0.7f)
                            lineTo(size.width / 2, size.height * 0.5f)
                            lineTo(0f, size.height * 0.7f)
                            close()
                        }
                        drawPath(path, AccentBlue)
                    }
                }
                
                Column {
                    Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        Box(Modifier.size(36.dp).clip(RoundedCornerShape(8.dp)).background(AccentBlue.copy(alpha = 0.25f)), Alignment.Center) {
                            Icon(Icons.Default.Map, null, AccentBlue, Modifier.size(22.dp))
                        }
                        Text("高德地图", TextPrimary, 14.sp, FontWeight.Medium)
                    }
                    Spacer(Modifier.height(6.dp))
                    Text("连接真实世界", TextPrimary, 22.sp, FontWeight.Bold)
                    Text("让出行更美好", TextSecondary, 12.sp)
                }
            }
        }
    }
}

// 媒体卡片
@Composable
private fun MediaCardView(onClick: () -> Unit) {
    Card(
        modifier = Modifier.fillMaxWidth().height(100.dp).clip(RoundedCornerShape(16.dp)).clickable(onClick = onClick),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = Color(0xDD2d3748))
    ) {
        Box(modifier = Modifier.fillMaxSize().background(Brush.linearGradient(listOf(Color(0xFF2d3748), Color(0xFF1a202c))))) {
            // 雪山背景
            Canvas(modifier = Modifier.fillMaxSize().align(Alignment.CenterEnd)) {
                val path = androidx.compose.ui.graphics.Path().apply {
                    moveTo(size.width * 0.3f, size.height * 0.7f)
                    lineTo(size.width * 0.5f, size.height * 0.2f)
                    lineTo(size.width * 0.7f, size.height * 0.7f)
                    close()
                }
                drawPath(path, Color.White.copy(alpha = 0.08f))
            }
            
            Text("暂无信息", TextSecondary, 13.sp, Modifier.align(Alignment.TopStart).padding(12.dp))
            
            Row(
                modifier = Modifier.fillMaxWidth().align(Alignment.BottomCenter).padding(8.dp),
                horizontalArrangement = Arrangement.SpaceEvenly, verticalAlignment = Alignment.CenterVertically
            ) {
                listOf(Icons.Default.GraphicEq, Icons.Default.Person, Icons.Default.SkipPrevious, Icons.Default.PlayArrow, Icons.Default.SkipNext, Icons.Default.Lock, Icons.Default.Close).forEachIndexed { i, icon ->
                    Box(
                        Modifier.size(if (i == 3) 36.dp else 28.dp).clip(CircleShape).background(if (i == 3) AccentBlue else Color.White.copy(alpha = 0.1f)),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(icon, null, Color.White, Modifier.size(if (i == 3) 20.dp else 14.dp))
                    }
                }
            }
        }
    }
}

// 竖屏媒体卡片
@Composable
private fun MediaCardPortrait(onClick: () -> Unit) {
    Card(
        modifier = Modifier.fillMaxWidth().height(80.dp).clip(RoundedCornerShape(16.dp)).clickable(onClick = onClick),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = Color(0xDD2d3748))
    ) {
        Row(
            modifier = Modifier.fillMaxSize().padding(12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(Modifier.size(48.dp).clip(RoundedCornerShape(10.dp)).background(Color.White.copy(alpha = 0.1f)), Alignment.Center) {
                Icon(Icons.Default.MusicNote, null, AccentGreen, Modifier.size(26.dp))
            }
            Spacer(Modifier.width(12.dp))
            Column(Modifier.weight(1f)) {
                Text("正在播放", TextSecondary, 11.sp)
                Text("点击播放音乐", TextPrimary, 14.sp, FontWeight.Bold)
            }
            Box(Modifier.size(36.dp).clip(CircleShape).background(AccentBlue), contentAlignment = Alignment.Center) {
                Icon(Icons.Default.PlayArrow, null, Color.White, Modifier.size(20.dp))
            }
        }
    }
}

// 应用网格
@Composable
private fun AppGridView(apps: List<AppInfo>, columns: Int, onClick: (AppInfo) -> Unit) {
    LazyVerticalGrid(
        columns = GridCells.Fixed(columns),
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(8.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp),
        userScrollEnabled = false
    ) {
        items(apps) { app ->
            AppIconCard(app) { onClick(app) }
        }
    }
}

@Composable
private fun AppIconCard(app: AppInfo, onClick: () -> Unit) {
    Column(
        modifier = Modifier.clip(RoundedCornerShape(12.dp)).background(Color.White.copy(alpha = 0.05f)).clickable(onClick = onClick).padding(8.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Box(
            Modifier.size(40.dp).clip(RoundedCornerShape(10.dp)).background(Brush.linearGradient(listOf(AccentBlue.copy(alpha = 0.8f), AccentBlue.copy(alpha = 0.4f)))),
            contentAlignment = Alignment.Center
        ) {
            Text(app.name.take(1), Color.White, 16.sp, FontWeight.Bold)
        }
        Spacer(Modifier.height(4.dp))
        Text(app.name, TextPrimary, 10.sp, maxLines = 1, overflow = TextOverflow.Ellipsis, textAlign = TextAlign.Center)
    }
}

// 底部Dock栏
@Composable
private fun BottomDock(selected: BottomNavTab, onSelect: (BottomNavTab) -> Unit) {
    Surface(
        modifier = Modifier.fillMaxWidth(),
        color = BottomBarBackground,
        shape = RoundedCornerShape(topStart = 20.dp, topEnd = 20.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth().padding(horizontal = 20.dp, vertical = 12.dp),
            horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically
        ) {
            // 左侧应用
            Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                listOf(Icons.Default.Apps, Icons.Default.Settings, Icons.Default.Palette, Icons.Default.MusicNote, Icons.Default.Apps).forEach { icon ->
                    Icon(icon, null, BottomBarIcon, Modifier.size(22.dp))
                }
            }
            
            // 黑胶唱片
            Box(
                Modifier.size(48.dp).clip(CircleShape).background(Brush.radialGradient(listOf(Color(0xFF333333), Color(0xFF1a1a1a)))).border(2.dp, AccentBlue, CircleShape),
                contentAlignment = Alignment.Center
            ) {
                Box(Modifier.size(12.dp).clip(CircleShape).background(Color(0xFF333333)).border(1.dp, Color.White.copy(alpha = 0.3f), CircleShape))
            }
            
            // 右侧应用
            Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                listOf(Icons.Default.Store, Icons.Default.DirectionsCar, Icons.Default.Home, Icons.Default.Tv, Icons.Default.Navigation, Icons.Default.Add).forEach { icon ->
                    Icon(icon, null, BottomBarIcon, Modifier.size(22.dp))
                }
            }
        }
    }
}

// 系统功能栏
@Composable
private fun SystemBar(onHome: () -> Unit, onNav: () -> Unit, onRefresh: () -> Unit, onLock: () -> Unit, onRotate: () -> Unit, onVolume: () -> Unit) {
    Surface(
        modifier = Modifier.fillMaxWidth(),
        color = Color(0xFF0d1117),
        shape = RoundedCornerShape(topStart = 16.dp, topEnd = 16.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth().padding(horizontal = 24.dp, vertical = 10.dp),
            horizontalArrangement = Arrangement.SpaceEvenly, verticalAlignment = Alignment.CenterVertically
        ) {
            listOf(
                Icons.Default.Home to "主页",
                Icons.Default.Navigation to "导航",
                Icons.Default.Refresh to "刷新",
                Icons.Default.Lock to "锁定",
                Icons.Default.ScreenRotation to "旋转",
                Icons.Default.VolumeUp to "音量"
            ).forEachIndexed { i, (icon, label) ->
                val onClick = when(i) { 0 -> onHome; 1 -> onNav; 2 -> onRefresh; 3 -> onLock; 4 -> onRotate; else -> onVolume }
                Column(
                    modifier = Modifier.clip(RoundedCornerShape(8.dp)).clickable(onClick = onClick).padding(6.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Icon(icon, label, TextSecondary, Modifier.size(22.dp))
                    Spacer(Modifier.height(2.dp))
                    Text(label, TextSecondary, 9.sp)
                }
            }
        }
    }
}
