package com.autohub.launcher.ui.main

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountCircle
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.MusicNote
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.filled.VideoLibrary
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.autohub.launcher.domain.model.AppInfo
import com.autohub.launcher.domain.model.WeatherInfo
import com.autohub.launcher.ui.theme.rememberScreenDimensions

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

    Scaffold(
        modifier = Modifier.fillMaxSize(),
        containerColor = MaterialTheme.colorScheme.background,
        topBar = {
            TopBar(
                time = currentTime,
                weather = uiState.weather,
                onSettingsClick = onNavigateToSettings,
                onProfileClick = onNavigateToProfile,
                screenDims = screenDims
            )
        },
        bottomBar = {
            BottomNavigation(
                selectedTab = uiState.selectedTab,
                onTabSelected = { tab ->
                    viewModel.onBottomNavTabSelected(tab)
                    when (tab) {
                        BottomNavTab.Navigation -> onNavigateToNavigation()
                        BottomNavTab.Music -> onNavigateToMusic()
                        BottomNavTab.Video -> onNavigateToVideo()
                        BottomNavTab.Settings -> onNavigateToSettings()
                        BottomNavTab.Home -> {}
                    }
                },
                screenDims = screenDims
            )
        }
    ) { paddingValues ->
        when (uiState.selectedTab) {
            BottomNavTab.Home -> HomeContent(
                modifier = Modifier.padding(paddingValues),
                apps = uiState.installedApps,
                onAppClicked = { app -> viewModel.onAppClicked(app) },
                screenDims = screenDims
            )
            BottomNavTab.Navigation -> PlaceholderContent("导航模块", screenDims)
            BottomNavTab.Music -> PlaceholderContent("音乐模块", screenDims)
            BottomNavTab.Video -> PlaceholderContent("视频模块", screenDims)
            BottomNavTab.Settings -> PlaceholderContent("设置模块", screenDims)
        }
    }
}

@Composable
private fun TopBar(
    time: String,
    weather: WeatherInfo?,
    onSettingsClick: () -> Unit,
    onProfileClick: () -> Unit,
    screenDims: com.autohub.launcher.ui.theme.ScreenDimensions
) {
    val padding = screenDims.contentPadding
    val baseFontSize = with(screenDims) { scaledSize(16.sp.value.dp) }
    val baseFontSizeSp = baseFontSize.value.sp
    
    Surface(
        modifier = Modifier.fillMaxWidth(),
        color = MaterialTheme.colorScheme.surface,
        shadowElevation = 2.dp
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(padding),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Row(
                horizontalArrangement = Arrangement.spacedBy(screenDims.scaledSize(16.dp)),
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Time
                Text(
                    text = time,
                    fontSize = (baseFontSizeSp.value * 1.75f).sp,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onSurface
                )

                // Weather
                weather?.let {
                    Row(
                        horizontalArrangement = Arrangement.spacedBy(screenDims.scaledSize(8.dp)),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "${it.temperature}°",
                            fontSize = (baseFontSizeSp.value * 1.25f).sp,
                            fontWeight = FontWeight.Medium
                        )
                        Text(
                            text = it.condition,
                            fontSize = baseFontSizeSp
                        )
                    }
                }
            }

            Row(
                horizontalArrangement = Arrangement.spacedBy(screenDims.scaledSize(8.dp)),
                verticalAlignment = Alignment.CenterVertically
            ) {
                // User avatar
                IconButton(onClick = onProfileClick) {
                    Icon(
                        imageVector = Icons.Default.AccountCircle,
                        contentDescription = "个人中心",
                        tint = MaterialTheme.colorScheme.onSurface,
                        modifier = Modifier.size(screenDims.largeIconSize)
                    )
                }

                // Settings button
                IconButton(onClick = onSettingsClick) {
                    Icon(
                        imageVector = Icons.Default.Settings,
                        contentDescription = "Settings",
                        tint = MaterialTheme.colorScheme.onSurface,
                        modifier = Modifier.size(screenDims.iconSize)
                    )
                }
            }
        }
    }
}

@Composable
private fun HomeContent(
    modifier: Modifier = Modifier,
    apps: List<AppInfo>,
    onAppClicked: (AppInfo) -> Unit,
    screenDims: com.autohub.launcher.ui.theme.ScreenDimensions
) {
    val padding = screenDims.contentPadding
    val spacing = screenDims.scaledSize(12.dp)
    
    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(padding),
        verticalArrangement = Arrangement.spacedBy(spacing)
    ) {
        // Smart Cards
        SmartCardsSection(screenDims)

        // Quick Apps Grid
        SectionTitle("快捷应用", screenDims)
        LazyVerticalGrid(
            columns = GridCells.Fixed(screenDims.gridColumns),
            horizontalArrangement = Arrangement.spacedBy(spacing),
            verticalArrangement = Arrangement.spacedBy(spacing),
            modifier = Modifier.weight(1f)
        ) {
            items(apps) { app ->
                AppGridItem(app = app, onClick = { onAppClicked(app) }, screenDims = screenDims)
            }
        }

        // Background Apps
        if (apps.isNotEmpty()) {
            SectionTitle("后台应用", screenDims)
            BackgroundAppsBar(apps = apps, screenDims = screenDims)
        }
    }
}

@Composable
private fun SmartCardsSection(screenDims: com.autohub.launcher.ui.theme.ScreenDimensions) {
    val spacing = screenDims.scaledSize(12.dp)
    
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(spacing)
    ) {
        // Weather Card
        SmartCard(
            modifier = Modifier.weight(1f),
            title = "天气",
            icon = "☀️",
            content = "25°C 晴\n深圳·南山区",
            screenDims = screenDims
        )

        // Schedule Card
        SmartCard(
            modifier = Modifier.weight(1f),
            title = "日程",
            icon = "📅",
            content = "14:00 会议\n科技园B座",
            screenDims = screenDims
        )
    }
}

@Composable
private fun SmartCard(
    modifier: Modifier = Modifier,
    title: String,
    icon: String,
    content: String,
    screenDims: com.autohub.launcher.ui.theme.ScreenDimensions
) {
    val cardHeight = screenDims.scaledSize(100.dp)
    val padding = screenDims.scaledSize(12.dp)
    val cornerRadius = screenDims.cardCornerRadius
    val fontSize = with(screenDims) { scaledSize(14.sp.value.dp).value.sp }
    
    Surface(
        modifier = modifier.height(cardHeight),
        shape = RoundedCornerShape(cornerRadius),
        color = MaterialTheme.colorScheme.surfaceVariant
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding),
            verticalArrangement = Arrangement.SpaceBetween
        ) {
            Text(
                text = "$icon $title",
                fontSize = fontSize,
                fontWeight = FontWeight.Medium
            )
            Text(
                text = content,
                fontSize = (fontSize.value * 0.875f).sp,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}

@Composable
private fun AppGridItem(
    app: AppInfo,
    onClick: () -> Unit,
    screenDims: com.autohub.launcher.ui.theme.ScreenDimensions
) {
    val iconSize = screenDims.scaledSize(48.dp)
    val spacing = screenDims.scaledSize(6.dp)
    val fontSize = with(screenDims) { scaledSize(11.sp.value.dp).value.sp }
    
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(screenDims.cardCornerRadius))
            .background(MaterialTheme.colorScheme.primaryContainer)
            .padding(vertical = spacing),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Box(
            modifier = Modifier
                .size(iconSize)
                .clip(CircleShape)
                .background(MaterialTheme.colorScheme.primary),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = app.name.first().toString(),
                fontSize = (fontSize.value * 1.5f).sp,
                color = Color.White
            )
        }
        Spacer(modifier = Modifier.height(spacing))
        Text(
            text = app.name,
            fontSize = fontSize,
            maxLines = 1,
            textAlign = TextAlign.Center
        )
    }
}

@Composable
private fun BackgroundAppsBar(
    apps: List<AppInfo>,
    screenDims: com.autohub.launcher.ui.theme.ScreenDimensions
) {
    val padding = screenDims.scaledSize(10.dp)
    val spacing = screenDims.scaledSize(12.dp)
    val fontSize = with(screenDims) { scaledSize(11.sp.value.dp).value.sp }
    
    Surface(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(screenDims.cardCornerRadius),
        color = MaterialTheme.colorScheme.surfaceVariant
    ) {
        Row(
            modifier = Modifier.padding(padding),
            horizontalArrangement = Arrangement.spacedBy(spacing)
        ) {
            apps.take(5).forEach { app ->
                Text(text = "🗺️ ${app.name}", fontSize = fontSize)
            }
        }
    }
}

@Composable
private fun SectionTitle(
    title: String,
    screenDims: com.autohub.launcher.ui.theme.ScreenDimensions
) {
    val fontSize = with(screenDims) { scaledSize(16.sp.value.dp).value.sp }
    val padding = screenDims.scaledSize(6.dp)
    
    Text(
        text = title,
        fontSize = fontSize,
        fontWeight = FontWeight.Bold,
        modifier = Modifier.padding(vertical = padding)
    )
}

@Composable
private fun PlaceholderContent(
    title: String,
    screenDims: com.autohub.launcher.ui.theme.ScreenDimensions
) {
    val fontSize = with(screenDims) { scaledSize(20.sp.value.dp).value.sp }
    
    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = title,
            fontSize = fontSize,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
    }
}

@Composable
private fun BottomNavigation(
    selectedTab: BottomNavTab,
    onTabSelected: (BottomNavTab) -> Unit,
    screenDims: com.autohub.launcher.ui.theme.ScreenDimensions
) {
    val padding = screenDims.scaledSize(8.dp)
    
    Surface(
        modifier = Modifier.fillMaxWidth(),
        shadowElevation = 4.dp
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = padding),
            horizontalArrangement = Arrangement.SpaceEvenly
        ) {
            BottomNavItem(
                icon = Icons.Default.Home,
                label = "首页",
                isSelected = selectedTab == BottomNavTab.Home,
                onClick = { onTabSelected(BottomNavTab.Home) },
                screenDims = screenDims
            )
            BottomNavItem(
                icon = Icons.Default.LocationOn,
                label = "导航",
                isSelected = selectedTab == BottomNavTab.Navigation,
                onClick = { onTabSelected(BottomNavTab.Navigation) },
                screenDims = screenDims
            )
            BottomNavItem(
                icon = Icons.Default.MusicNote,
                label = "音乐",
                isSelected = selectedTab == BottomNavTab.Music,
                onClick = { onTabSelected(BottomNavTab.Music) },
                screenDims = screenDims
            )
            BottomNavItem(
                icon = Icons.Default.VideoLibrary,
                label = "视频",
                isSelected = selectedTab == BottomNavTab.Video,
                onClick = { onTabSelected(BottomNavTab.Video) },
                screenDims = screenDims
            )
            BottomNavItem(
                icon = Icons.Default.Settings,
                label = "设置",
                isSelected = selectedTab == BottomNavTab.Settings,
                onClick = { onTabSelected(BottomNavTab.Settings) },
                screenDims = screenDims
            )
        }
    }
}

@Composable
private fun BottomNavItem(
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    label: String,
    isSelected: Boolean,
    onClick: () -> Unit,
    screenDims: com.autohub.launcher.ui.theme.ScreenDimensions
) {
    val iconSize = screenDims.iconSize
    val fontSize = with(screenDims) { scaledSize(10.sp.value.dp).value.sp }
    
    Column(
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        IconButton(onClick = onClick, modifier = Modifier.size(iconSize * 2)) {
            Icon(
                imageVector = icon,
                contentDescription = label,
                tint = if (isSelected) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurfaceVariant,
                modifier = Modifier.size(iconSize)
            )
        }
        Text(
            text = label,
            fontSize = fontSize,
            color = if (isSelected) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurfaceVariant
        )
    }
}
