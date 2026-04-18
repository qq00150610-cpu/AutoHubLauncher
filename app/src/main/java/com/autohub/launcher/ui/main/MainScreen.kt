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
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.drawscope.Stroke
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
            LandscapeNewUI(viewModel, uiState, currentTime)
        } else {
            PortraitNewUI(viewModel, uiState, currentTime)
        }
    }
}

// ==================== 横屏布局 ====================
@Composable
private fun LandscapeNewUI(
    viewModel: MainViewModel,
    uiState: MainUiState,
    currentTime: String
) {
    Column(modifier = Modifier.fillMaxSize()) {
        // 顶部状态栏
        LandscapeTopBar(currentTime, uiState.weather) { viewModel.openSettings() }
        
        // 主内容区
        Row(
            modifier = Modifier
                .weight(1f)
                .fillMaxWidth()
                .padding(horizontal = 16.dp),
            horizontalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // 左侧地图弹窗 (45%)
            LandscapeMapCard(
                modifier = Modifier.weight(0.45f).fillMaxHeight(),
                onClick = { viewModel.openNavigationApp() }
            )
            
            // 右侧 (55%)
            Column(
                modifier = Modifier.weight(0.55f).fillMaxHeight(),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                // 媒体卡片
                LandscapeMediaCard(
                    modifier = Modifier.fillMaxWidth().height(140.dp),
                    onClick = { viewModel.openMusicApp() }
                )
                
                // 应用网格
                AppGridLandscape(uiState.installedApps) { viewModel.onAppClicked(it) }
            }
        }
        
        // 底部应用栏
        BottomDockBar(
            selectedTab = uiState.selectedTab,
            onTabSelected = { viewModel.onBottomNavTabSelected(it) }
        )
    }
}

@Composable
private fun LandscapeTopBar(time: String, weather: WeatherInfo?, onSettingsClick: () -> Unit) {
    Row(
        modifier = Modifier.fillMaxWidth().padding(horizontal = 20.dp, vertical = 12.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = time,
            color = TextPrimary,
            fontSize = 32.sp,
            fontWeight = FontWeight.Bold
        )
        
        Row(
            horizontalArrangement = Arrangement.spacedBy(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            weather?.let {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        Icons.Default.WbSunny,
                        contentDescription = null,
                        tint = AccentOrange,
                        modifier = Modifier.size(22.dp)
                    )
                    Spacer(Modifier.width(4.dp))
                    Text(
                        text = "${it.temperature}°",
                        color = TextPrimary,
                        fontSize = 18.sp
                    )
                    Spacer(Modifier.width(8.dp))
                    Text(
                        text = it.location ?: "昆山市",
                        color = TextSecondary,
                        fontSize = 14.sp
                    )
                }
            }
            
            IconButton(onClick = onSettingsClick) {
                Icon(
                    Icons.Default.Settings,
                    contentDescription = "设置",
                    tint = TextPrimary,
                    modifier = Modifier.size(28.dp)
                )
            }
        }
    }
}

// ==================== 地图弹窗组件 ====================
@Composable
private fun LandscapeMapCard(modifier: Modifier = Modifier, onClick: () -> Unit) {
    Card(
        modifier = modifier
            .clip(RoundedCornerShape(24.dp))
            .clickable(onClick = onClick),
        shape = RoundedCornerShape(24.dp),
        colors = CardDefaults.cardColors(containerColor = Color.Transparent)
    ) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(Brush.linearGradient(listOf(Color(0xFF1a2744), Color(0xFF0f1629))))
        ) {
            // 背景路网纹理
            RoadNetworkPattern(modifier = Modifier.fillMaxSize())
            
            // 关闭按钮
            IconButton(
                onClick = { },
                modifier = Modifier
                    .align(Alignment.TopEnd)
                    .padding(12.dp)
                    .size(32.dp)
                    .clip(CircleShape)
                    .background(Color.White.copy(alpha = 0.1f))
            ) {
                Icon(Icons.Default.Close, "关闭", Color.White, Modifier.size(18.dp))
            }
            
            // 主内容
            Row(
                modifier = Modifier.fillMaxSize().padding(24.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                // 左侧
                Column(modifier = Modifier.weight(1f), verticalArrangement = Arrangement.spacedBy(16.dp)) {
                    // 图标 + 应用名
                    Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                        Box(
                            modifier = Modifier
                                .size(48.dp)
                                .clip(RoundedCornerShape(12.dp))
                                .background(AccentBlue.copy(alpha = 0.2f)),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(Icons.Default.Map, null, AccentBlue, Modifier.size(28.dp))
                        }
                        Text("高德地图", TextPrimary, 18.sp, FontWeight.Medium)
                    }
                    
                    Text("连接真实世界", TextPrimary, 28.sp, FontWeight.Bold)
                    Text("让出行更美好", TextSecondary, 16.sp)
                    
                    Spacer(Modifier.height(16.dp))
                    
                    Button(
                        onClick = onClick,
                        colors = ButtonDefaults.buttonColors(containerColor = AccentBlue),
                        shape = RoundedCornerShape(24.dp),
                        contentPadding = PaddingValues(horizontal = 20.dp, vertical = 12.dp)
                    ) {
                        Icon(Icons.Default.Send, null, Modifier.size(18.dp))
                        Spacer(Modifier.width(8.dp))
                        Text("打开地图", 14.sp, FontWeight.Medium)
                    }
                }
                
                // 右侧：立体导航箭头
                NavigationArrow3D(Modifier.size(120.dp))
            }
        }
    }
}

@Composable
private fun RoadNetworkPattern(modifier: Modifier = Modifier) {
    Canvas(modifier = modifier) {
        val lineColor = Color.White.copy(alpha = 0.05f)
        val gridSize = 40.dp.toPx()
        
        var y = 0f
        while (y < size.height) {
            drawLine(lineColor, Offset(0f, y), Offset(size.width, y), 1.dp.toPx())
            y += gridSize
        }
        
        var x = 0f
        while (x < size.width) {
            drawLine(lineColor, Offset(x, 0f), Offset(x, size.height), 1.dp.toPx())
            x += gridSize
        }
        
        drawArc(
            color = AccentBlue.copy(alpha = 0.1f),
            startAngle = 0f, sweepAngle = 90f, useCenter = false,
            topLeft = Offset(size.width * 0.3f, size.height * 0.2f),
            size = androidx.compose.ui.geometry.Size(size.width * 0.4f, size.height * 0.3f),
            style = Stroke(width = 3.dp.toPx())
        )
    }
}

@Composable
private fun NavigationArrow3D(modifier: Modifier = Modifier) {
    Box(modifier, contentAlignment = Alignment.Center) {
        Box(
            Modifier.size(100.dp).clip(CircleShape).background(
                Brush.radialGradient(listOf(AccentBlue.copy(alpha = 0.3f), AccentBlue.copy(alpha = 0f)))
            )
        )
        
        Canvas(Modifier.size(60.dp)) {
            val path = Path().apply {
                moveTo(size.width / 2, 0f)
                lineTo(size.width, size.height * 0.7f)
                lineTo(size.width / 2, size.height * 0.5f)
                lineTo(0f, size.height * 0.7f)
                close()
            }
            drawPath(path, Brush.verticalGradient(listOf(AccentBlue, AccentBlue.copy(alpha = 0.7f))))
            
            val highlightPath = Path().apply {
                moveTo(size.width / 2, size.height * 0.1f)
                lineTo(size.width * 0.7f, size.height * 0.6f)
                lineTo(size.width / 2, size.height * 0.45f)
                lineTo(size.width * 0.3f, size.height * 0.6f)
                close()
            }
            drawPath(highlightPath, Color.White.copy(alpha = 0.3f))
        }
    }
}

// ==================== 媒体播放弹窗 ====================
@Composable
private fun LandscapeMediaCard(modifier: Modifier = Modifier, onClick: () -> Unit) {
    Card(
        modifier = modifier
            .clip(RoundedCornerShape(20.dp))
            .clickable(onClick = onClick),
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(containerColor = Color.Transparent)
    ) {
        Box(
            Modifier.fillMaxSize().background(
                Brush.linearGradient(listOf(Color(0xFF2d3748), Color(0xFF1a202c)))
            )
        ) {
            // 雪山背景
            Box(Modifier.fillMaxSize().align(Alignment.CenterEnd)) {
                Box(
                    Modifier.fillMaxSize().background(
                        Brush.horizontalGradient(listOf(Color(0xFF2d3748), Color(0x001a202c)))
                    )
                )
                
                Canvas(Modifier.fillMaxSize()) {
                    val mountainPath = Path().apply {
                        moveTo(size.width * 0.5f, size.height * 0.2f)
                        lineTo(size.width * 0.8f, size.height * 0.8f)
                        lineTo(size.width * 0.2f, size.height * 0.8f)
                        close()
                    }
                    drawPath(mountainPath, Brush.verticalGradient(
                        listOf(Color.White.copy(alpha = 0.15f), Color.White.copy(alpha = 0.05f))
                    ))
                }
            }
            
            Text("暂无信息", TextSecondary, 14.sp, Modifier.align(Alignment.TopStart).padding(16.dp))
            
            Row(
                Modifier.fillMaxWidth().align(Alignment.BottomCenter).padding(12.dp),
                horizontalArrangement = Arrangement.SpaceEvenly, verticalAlignment = Alignment.CenterVertically
            ) {
                MediaControlBtn(Icons.Default.GraphicEq, "波形")
                MediaControlBtn(Icons.Default.Person, "头像")
                MediaControlBtn(Icons.Default.SkipPrevious, "上一曲")
                MediaControlBtn(Icons.Default.PlayArrow, "播放", true, onClick)
                MediaControlBtn(Icons.Default.SkipNext, "下一曲")
                MediaControlBtn(Icons.Default.Lock, "锁定")
                MediaControlBtn(Icons.Default.Close, "关闭")
            }
        }
    }
}

@Composable
private fun MediaControlBtn(icon: ImageVector, desc: String, isPrimary: Boolean = false, onClick: () -> Unit = {}) {
    Box(
        Modifier
            .size(if (isPrimary) 40.dp else 32.dp)
            .clip(CircleShape)
            .background(if (isPrimary) AccentBlue else Color.White.copy(alpha = 0.1f))
            .clickable(onClick = onClick),
        contentAlignment = Alignment.Center
    ) {
        Icon(icon, desc, Color.White, Modifier.size(if (isPrimary) 24.dp else 18.dp))
    }
}

// ==================== 应用网格 ====================
@Composable
private fun AppGridLandscape(apps: List<AppInfo>, onAppClick: (AppInfo) -> Unit) {
    LazyVerticalGrid(
        columns = GridCells.Fixed(5),
        modifier = Modifier.fillMaxWidth().weight(1f),
        horizontalArrangement = Arrangement.spacedBy(8.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp),
        userScrollEnabled = false
    ) {
        items(apps.take(10)) { app ->
            AppIconItem(app, Modifier.aspectRatio(1f)) { onAppClick(app) }
        }
    }
}

@Composable
private fun AppIconItem(app: AppInfo, modifier: Modifier = Modifier, onClick: () -> Unit) {
    Column(
        modifier = modifier
            .clip(RoundedCornerShape(16.dp))
            .background(Color.White.copy(alpha = 0.05f))
            .clickable(onClick = onClick)
            .padding(8.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Box(
            Modifier.size(44.dp).clip(RoundedCornerShape(12.dp))
                .background(Brush.linearGradient(listOf(AccentBlue.copy(alpha = 0.8f), AccentBlue.copy(alpha = 0.4f)))),
            contentAlignment = Alignment.Center
        ) {
            Text(app.name.take(1), Color.White, 18.sp, FontWeight.Bold)
        }
        Spacer(Modifier.height(6.dp))
        Text(app.name, TextPrimary, 11.sp, maxLines = 1, overflow = TextOverflow.Ellipsis, textAlign = TextAlign.Center)
    }
}

// ==================== 底部 Dock 栏 ====================
@Composable
private fun BottomDockBar(selectedTab: BottomNavTab, onTabSelected: (BottomNavTab) -> Unit) {
    Surface(
        modifier = Modifier.fillMaxWidth(),
        color = BottomBarBackground,
        shape = RoundedCornerShape(topStart = 24.dp, topEnd = 24.dp)
    ) {
        Row(
            Modifier.fillMaxWidth().padding(horizontal = 24.dp, vertical = 16.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            // 左侧应用图标
            Row(horizontalArrangement = Arrangement.spacedBy(16.dp), verticalAlignment = Alignment.CenterVertically) {
                DockIcon(Icons.Default.Apps, "我的应用")
                DockIcon(Icons.Default.Settings, "系统设置")
                DockIcon(Icons.Default.Palette, "主题中心")
                DockIcon(Icons.Default.MusicNote, "酷我VIP")
                DockIcon(Icons.Default.Apps, "轻应用")
            }
            
            // 中间黑胶唱片
            VinylRecordPlayer()
            
            // 右侧应用图标
            Row(horizontalArrangement = Arrangement.spacedBy(16.dp), verticalAlignment = Alignment.CenterVertically) {
                DockIcon(Icons.Default.Store, "应用商店")
                DockIcon(Icons.Default.DirectionsCar, "车主服务")
                DockIcon(Icons.Default.Home, "返回桌面")
                DockIcon(Icons.Default.Tv, "沙发管家")
                DockIcon(Icons.Default.Navigation, "百度CarLife")
                DockIcon(Icons.Default.Add, "添加")
            }
        }
    }
}

@Composable
private fun DockIcon(icon: ImageVector, desc: String) {
    Column(
        Modifier.clip(RoundedCornerShape(8.dp)).clickable { }.padding(4.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Icon(icon, desc, BottomBarIcon, Modifier.size(24.dp))
    }
}

@Composable
private fun VinylRecordPlayer() {
    Box(
        Modifier.size(56.dp).clip(CircleShape)
            .background(Brush.radialGradient(listOf(Color(0xFF333333), Color(0xFF1a1a1a))))
            .border(2.dp, AccentBlue, CircleShape),
        contentAlignment = Alignment.Center
    ) {
        Box(
            Modifier.size(16.dp).clip(CircleShape).background(Color(0xFF333333))
                .border(1.dp, Color.White.copy(alpha = 0.3f), CircleShape)
        )
        
        Canvas(Modifier.fillMaxSize()) {
            for (i in 0..2) {
                drawCircle(Color.White.copy(alpha = 0.05f), (size.minDimension / 2) * (0.4f + i * 0.15f), style = Stroke(0.5.dp.toPx()))
            }
        }
    }
}

// ==================== 竖屏布局 ====================
@Composable
private fun PortraitNewUI(viewModel: MainViewModel, uiState: MainUiState, currentTime: String) {
    Column(modifier = Modifier.fillMaxSize()) {
        // 顶部状态栏
        PortraitTopBar(currentTime, uiState.weather) { viewModel.openSettings() }
        
        // 主内容区
        Column(
            Modifier.weight(1f).fillMaxWidth().verticalScroll(rememberScrollState()).padding(horizontal = 16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Spacer(Modifier.height(8.dp))
            
            PortraitMapCard(Modifier.fillMaxWidth().height(200.dp)) { viewModel.openNavigationApp() }
            PortraitMediaCard(Modifier.fillMaxWidth().height(120.dp)) { viewModel.openMusicApp() }
            
            Text("快捷应用", TextPrimary, 16.sp, FontWeight.Medium, modifier = Modifier.padding(horizontal = 4.dp))
            
            PortraitAppGrid(uiState.installedApps) { viewModel.onAppClicked(it) }
            
            Spacer(Modifier.height(8.dp))
        }
        
        SystemFunctionBar(
            onHomeClick = { },
            onNavClick = { viewModel.openNavigationApp() },
            onRefreshClick = { viewModel.onResume() },
            onLockClick = { },
            onRotateClick = { },
            onVolumeClick = { }
        )
    }
}

@Composable
private fun PortraitTopBar(time: String, weather: WeatherInfo?, onSettingsClick: () -> Unit) {
    Row(
        Modifier.fillMaxWidth().padding(horizontal = 20.dp, vertical = 16.dp),
        horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically
    ) {
        // 左侧
        Row(horizontalArrangement = Arrangement.spacedBy(12.dp), verticalAlignment = Alignment.CenterVertically) {
            Box(Modifier.size(36.dp).clip(CircleShape).background(AccentBlue.copy(alpha = 0.3f)), Alignment.Center) {
                Icon(Icons.Default.Person, null, Color.White, Modifier.size(20.dp))
            }
            Icon(Icons.Default.SignalCellularAlt, null, AccentGreen, Modifier.size(18.dp))
            Icon(Icons.Default.Notifications, null, TextSecondary, Modifier.size(18.dp))
        }
        
        Text(time, TextPrimary, 28.sp, FontWeight.Bold)
        
        // 右侧
        Row(horizontalArrangement = Arrangement.spacedBy(12.dp), verticalAlignment = Alignment.CenterVertically) {
            weather?.let {
                Column(horizontalAlignment = Alignment.End) {
                    Text(it.location ?: "昆山市", TextSecondary, 12.sp)
                    Text("${it.temperature}°", TextPrimary, 14.sp)
                }
                Icon(Icons.Default.WbSunny, null, AccentOrange, Modifier.size(20.dp))
            }
            IconButton(onClick = onSettingsClick) {
                Icon(Icons.Default.Settings, "设置", TextPrimary, Modifier.size(24.dp))
            }
        }
    }
}

@Composable
private fun PortraitMapCard(modifier: Modifier = Modifier, onClick: () -> Unit) {
    Card(
        modifier = modifier
            .clip(RoundedCornerShape(24.dp))
            .clickable(onClick = onClick),
        shape = RoundedCornerShape(24.dp),
        colors = CardDefaults.cardColors(containerColor = Color.Transparent)
    ) {
        Box(
            Modifier.fillMaxSize().background(Brush.linearGradient(listOf(Color(0xFF1a2744), Color(0xFF0f1629))))
        ) {
            RoadNetworkPattern(Modifier.fillMaxSize())
            
            Column(Modifier.fillMaxSize().padding(20.dp), verticalArrangement = Arrangement.SpaceBetween) {
                IconButton(
                    onClick = { },
                    modifier = Modifier.align(Alignment.End).size(28.dp).clip(CircleShape).background(Color.White.copy(alpha = 0.1f))
                ) {
                    Icon(Icons.Default.Close, "关闭", Color.White, Modifier.size(16.dp))
                }
                
                Column {
                    Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                        Box(Modifier.size(40.dp).clip(RoundedCornerShape(10.dp)).background(AccentBlue.copy(alpha = 0.2f)), Alignment.Center) {
                            Icon(Icons.Default.Map, null, AccentBlue, Modifier.size(24.dp))
                        }
                        Text("高德地图", TextPrimary, 16.sp, FontWeight.Medium)
                    }
                    Spacer(Modifier.height(8.dp))
                    Text("连接真实世界", TextPrimary, 24.sp, FontWeight.Bold)
                    Text("让出行更美好", TextSecondary, 14.sp)
                }
            }
            
            NavigationArrow3D(Modifier.size(80.dp).align(Alignment.TopStart).padding(start = 20.dp, top = 40.dp))
        }
    }
}

@Composable
private fun PortraitMediaCard(modifier: Modifier = Modifier, onClick: () -> Unit) {
    Card(
        modifier = modifier
            .clip(RoundedCornerShape(20.dp))
            .clickable(onClick = onClick),
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(containerColor = Color.Transparent)
    ) {
        Box(
            Modifier.fillMaxSize().background(Brush.linearGradient(listOf(Color(0xFF2d3748), Color(0xFF1a202c))))
        ) {
            Text("暂无信息", TextSecondary, 14.sp, Modifier.align(Alignment.TopStart).padding(12.dp))
            
            Row(
                Modifier.fillMaxWidth().align(Alignment.BottomCenter).padding(8.dp),
                horizontalArrangement = Arrangement.SpaceEvenly, verticalAlignment = Alignment.CenterVertically
            ) {
                MediaControlBtn(Icons.Default.GraphicEq, "波形")
                MediaControlBtn(Icons.Default.Person, "头像")
                MediaControlBtn(Icons.Default.SkipPrevious, "上一曲")
                MediaControlBtn(Icons.Default.PlayArrow, "播放", true, onClick)
                MediaControlBtn(Icons.Default.SkipNext, "下一曲")
                MediaControlBtn(Icons.Default.Lock, "锁定")
            }
        }
    }
}

@Composable
private fun PortraitAppGrid(apps: List<AppInfo>, onAppClick: (AppInfo) -> Unit) {
    LazyVerticalGrid(
        columns = GridCells.Fixed(5),
        modifier = Modifier.fillMaxWidth().height(200.dp),
        horizontalArrangement = Arrangement.spacedBy(8.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp),
        userScrollEnabled = false
    ) {
        items(apps.take(10)) { app ->
            AppIconItemPortrait(app) { onAppClick(app) }
        }
    }
}

@Composable
private fun AppIconItemPortrait(app: AppInfo, onClick: () -> Unit) {
    Column(
        Modifier.clip(RoundedCornerShape(12.dp)).background(Color.White.copy(alpha = 0.05f)).clickable(onClick = onClick).padding(6.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Box(
            Modifier.size(40.dp).clip(RoundedCornerShape(10.dp))
                .background(Brush.linearGradient(listOf(AccentBlue.copy(alpha = 0.8f), AccentBlue.copy(alpha = 0.4f)))),
            contentAlignment = Alignment.Center
        ) {
            Text(app.name.take(1), Color.White, 16.sp, FontWeight.Bold)
        }
        Spacer(Modifier.height(4.dp))
        Text(app.name, TextPrimary, 10.sp, maxLines = 1, overflow = TextOverflow.Ellipsis, textAlign = TextAlign.Center)
    }
}

@Composable
private fun SystemFunctionBar(
    onHomeClick: () -> Unit,
    onNavClick: () -> Unit,
    onRefreshClick: () -> Unit,
    onLockClick: () -> Unit,
    onRotateClick: () -> Unit,
    onVolumeClick: () -> Unit
) {
    Surface(
        modifier = Modifier.fillMaxWidth(),
        color = Color(0xFF0d1117),
        shape = RoundedCornerShape(topStart = 20.dp, topEnd = 20.dp)
    ) {
        Row(
            Modifier.fillMaxWidth().padding(horizontal = 32.dp, vertical = 12.dp),
            horizontalArrangement = Arrangement.SpaceEvenly, verticalAlignment = Alignment.CenterVertically
        ) {
            FuncBtn(Icons.Default.Home, "主页", onHomeClick)
            FuncBtn(Icons.Default.Navigation, "导航", onNavClick)
            FuncBtn(Icons.Default.Refresh, "刷新", onRefreshClick)
            FuncBtn(Icons.Default.Lock, "锁定", onLockClick)
            FuncBtn(Icons.Default.ScreenRotation, "旋转", onRotateClick)
            FuncBtn(Icons.Default.VolumeUp, "音量", onVolumeClick)
        }
    }
}

@Composable
private fun FuncBtn(icon: ImageVector, desc: String, onClick: () -> Unit) {
    Column(
        Modifier.clip(RoundedCornerShape(8.dp)).clickable(onClick = onClick).padding(8.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Icon(icon, desc, TextSecondary, Modifier.size(24.dp))
        Spacer(Modifier.height(4.dp))
        Text(desc, TextSecondary, 10.sp)
    }
}
