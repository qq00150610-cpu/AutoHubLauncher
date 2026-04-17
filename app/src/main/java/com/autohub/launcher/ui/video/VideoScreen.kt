package com.autohub.launcher.ui.video

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
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import androidx.hilt.navigation.compose.hiltViewModel

@Composable
fun VideoScreen(
    viewModel: VideoViewModel = hiltViewModel(),
    onBack: () -> Unit = {}
) {
    val uiState by viewModel.uiState.collectAsState()

    Scaffold(
        modifier = Modifier.fillMaxSize(),
        topBar = {
            VideoTopBar(onBack = onBack)
        }
    ) { paddingValues ->
        Box(
            modifier = Modifier
                .padding(paddingValues)
                .fillMaxSize()
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                // Video Player Placeholder
                VideoPlayerPlaceholder(
                    isPlaying = uiState.isPlaying,
                    isPipMode = uiState.isPipMode
                )

                // Video Controls
                VideoControls(
                    isPlaying = uiState.isPlaying,
                    onPlayPause = {
                        if (uiState.isPlaying) {
                            viewModel.pauseVideo()
                        } else {
                            // Play video logic here
                        }
                    },
                    onPipMode = { }
                )

                // Video Apps
                if (uiState.videoApps.isNotEmpty()) {
                    SectionTitle("视频应用")
                    VideoAppsList(
                        apps = uiState.videoApps,
                        onAppClick = { viewModel.playVideo(it.packageName) }
                    )
                }
            }

            // Safety Warning Dialog
            if (uiState.safetyWarning) {
                SafetyWarningDialog(
                    onDismiss = { viewModel.dismissSafetyWarning() },
                    onAudioOnly = { viewModel.enableAudioOnly() },
                    onDisableRestriction = { viewModel.disableSafetyRestriction("8888") }
                )
            }
        }
    }
}

@Composable
private fun VideoTopBar(
    onBack: () -> Unit
) {
    TopAppBar(
        title = { Text("视频", fontWeight = FontWeight.Bold) },
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
private fun VideoPlayerPlaceholder(
    isPlaying: Boolean,
    isPipMode: Boolean
) {
    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .aspectRatio(16f / 9f),
        shape = RoundedCornerShape(16.dp),
        color = Color.Black
    ) {
        Box(
            modifier = Modifier.fillMaxSize(),
            contentAlignment = Alignment.Center
        ) {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                Icon(
                    if (isPlaying) Icons.Default.PauseCircle else Icons.Default.PlayCircle,
                    contentDescription = "播放/暂停",
                    modifier = Modifier.size(80.dp),
                    tint = Color.White
                )

                Text(
                    text = if (isPlaying) "视频播放中" else "点击播放视频",
                    color = Color.White,
                    fontSize = 16.sp
                )

                if (isPipMode) {
                    Surface(
                        color = Color(0xFFE91E63),
                        shape = RoundedCornerShape(4.dp)
                    ) {
                        Text(
                            text = "画中画模式",
                            color = Color.White,
                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                            fontSize = 12.sp
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun VideoControls(
    isPlaying: Boolean,
    onPlayPause: () -> Unit,
    onPipMode: () -> Unit
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceEvenly,
        verticalAlignment = Alignment.CenterVertically
    ) {
        IconButton(onClick = onPlayPause, modifier = Modifier.size(56.dp)) {
            Icon(
                if (isPlaying) Icons.Default.Pause else Icons.Default.PlayArrow,
                contentDescription = "播放/暂停",
                modifier = Modifier.size(32.dp)
            )
        }

        IconButton(onClick = onPipMode, modifier = Modifier.size(56.dp)) {
            Icon(
                Icons.Default.PictureInPicture,
                contentDescription = "画中画",
                modifier = Modifier.size(32.dp)
            )
        }
    }
}

@Composable
private fun SectionTitle(title: String) {
    Text(
        text = title,
        fontSize = 18.sp,
        fontWeight = FontWeight.Bold,
        modifier = Modifier.padding(vertical = 8.dp)
    )
}

@Composable
private fun VideoAppsList(
    apps: List<com.autohub.launcher.domain.model.AppInfo>,
    onAppClick: (com.autohub.launcher.domain.model.AppInfo) -> Unit
) {
    LazyColumn(
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        items(apps) { app ->
            VideoAppItem(app = app, onClick = { onAppClick(app) })
        }
    }
}

@Composable
private fun VideoAppItem(
    app: com.autohub.launcher.domain.model.AppInfo,
    onClick: () -> Unit
) {
    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .height(64.dp)
            .clickable(onClick = onClick),
        shape = RoundedCornerShape(12.dp),
        color = MaterialTheme.colorScheme.surfaceVariant
    ) {
        Row(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Box(
                modifier = Modifier
                    .size(48.dp)
                    .background(
                        Color(0xFF673AB7), // Video color
                        RoundedCornerShape(8.dp)
                    ),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = app.name.first().toString(),
                    fontSize = 20.sp,
                    color = Color.White,
                    fontWeight = FontWeight.Bold
                )
            }
            Text(
                text = app.name,
                fontSize = 16.sp,
                fontWeight = FontWeight.Medium,
                modifier = Modifier.weight(1f)
            )
            Icon(
                Icons.Default.ChevronRight,
                contentDescription = "打开",
                tint = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}

@Composable
private fun SafetyWarningDialog(
    onDismiss: () -> Unit,
    onAudioOnly: () -> Unit,
    onDisableRestriction: () -> Unit
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
                // Warning Icon
                Box(
                    modifier = Modifier
                        .size(64.dp)
                        .background(
                            Color(0xFFFF9800),
                            CircleShape
                        ),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        Icons.Default.Warning,
                        contentDescription = "警告",
                        modifier = Modifier.size(32.dp),
                        tint = Color.White
                    )
                }

                // Warning Message
                Text(
                    text = "安全提示",
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Bold
                )

                Text(
                    text = "检测到车辆正在行驶，视频已暂停播放。为确保行车安全，行驶过程中禁止观看视频。",
                    fontSize = 14.sp,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )

                // Action Buttons
                Column(
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Button(
                        onClick = onAudioOnly,
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text("仅播放音频")
                    }

                    OutlinedButton(
                        onClick = onDismiss,
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text("我知道了")
                    }

                    TextButton(
                        onClick = onDisableRestriction,
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text("关闭限制(需密码)")
                    }
                }
            }
        }
    }
}
