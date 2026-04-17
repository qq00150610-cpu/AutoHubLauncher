package com.autohub.launcher.ui.files

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
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel

@Composable
fun FilesScreen(
    viewModel: FilesViewModel = hiltViewModel(),
    onBack: () -> Unit = {}
) {
    val uiState by viewModel.uiState.collectAsState()

    Scaffold(
        modifier = Modifier.fillMaxSize(),
        topBar = {
            FilesTopBar(
                currentPath = uiState.currentPath,
                onBack = onBack,
                onNavigationUp = { viewModel.onNavigationUp() },
                onHomeNavigation = { viewModel.onHomeNavigation() }
            )
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .padding(paddingValues)
                .fillMaxSize(),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // Storage Info
            StorageInfoCard(
                storageInfo = uiState.storageInfo
            )

            // Files List
            LazyColumn(
                modifier = Modifier.weight(1f),
                contentPadding = PaddingValues(16.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                items(uiState.files) { file ->
                    FileItem(
                        file = file,
                        isSelected = uiState.selectedFiles.contains(file.path),
                        isSelectionMode = uiState.isSelectionMode,
                        onFileClick = { viewModel.onFileClicked(file) },
                        onFileLongClick = { viewModel.onFileSelectionToggled(file.path) }
                    )
                }
            }

            // Bottom Actions
            if (uiState.isSelectionMode && uiState.selectedFiles.isNotEmpty()) {
                SelectionActionsBar(
                    selectedCount = uiState.selectedFiles.size,
                    onDelete = { viewModel.onDeleteSelected() },
                    onClearSelection = { viewModel.clearSelection() }
                )
            }
        }
    }
}

@Composable
private fun FilesTopBar(
    currentPath: String,
    onBack: () -> Unit,
    onNavigationUp: () -> Unit,
    onHomeNavigation: () -> Unit
) {
    TopAppBar(
        title = {
            Column {
                Text(
                    text = "文件管理",
                    fontWeight = FontWeight.Bold,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
                Text(
                    text = currentPath,
                    fontSize = 12.sp,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
            }
        },
        navigationIcon = {
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                IconButton(onClick = onBack) {
                    Icon(Icons.Default.ArrowBack, contentDescription = "返回")
                }
                IconButton(onClick = onNavigationUp) {
                    Icon(Icons.Default.ArrowUpward, contentDescription = "上级目录")
                }
            }
        },
        actions = {
            IconButton(onClick = onHomeNavigation) {
                Icon(Icons.Default.Home, contentDescription = "主页")
            }
        },
        colors = TopAppBarDefaults.topAppBarColors(
            containerColor = MaterialTheme.colorScheme.surface
        )
    )
}

@Composable
private fun StorageInfoCard(
    storageInfo: com.autohub.launcher.data.local.StorageInfo?
) {
    storageInfo?.let { info ->
        Surface(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp),
            shape = RoundedCornerShape(12.dp),
            color = MaterialTheme.colorScheme.surfaceVariant
        ) {
            Column(
                modifier = Modifier.padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                Text(
                    text = "存储空间",
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold
                )

                LinearProgressIndicator(
                    progress = info.usedPercentage / 100f,
                    modifier = Modifier.fillMaxWidth(),
                    color = when {
                        info.usedPercentage > 90 -> Color(0xFFF44336)
                        info.usedPercentage > 70 -> Color(0xFFFF9800)
                        else -> MaterialTheme.colorScheme.primary
                    }
                )

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text(
                        text = "已用: ${info.formatUsedSpace()}",
                        fontSize = 12.sp,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    Text(
                        text = "总计: ${info.formatTotalSpace()}",
                        fontSize = 12.sp,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }
        }
    }
}

@Composable
private fun FileItem(
    file: com.autohub.launcher.data.local.FileItem,
    isSelected: Boolean,
    isSelectionMode: Boolean,
    onFileClick: () -> Unit,
    onFileLongClick: () -> Unit
) {
    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .height(64.dp)
            .clickable(onClick = onFileClick),
        shape = RoundedCornerShape(12.dp),
        color = if (isSelected) {
            MaterialTheme.colorScheme.primaryContainer
        } else {
            MaterialTheme.colorScheme.surfaceVariant
        }
    ) {
        Row(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp),
            horizontalArrangement = Arrangement.spacedBy(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Selection Checkbox
            if (isSelectionMode) {
                Checkbox(
                    checked = isSelected,
                    onCheckedChange = { onFileLongClick() }
                )
            }

            // File Icon
            Box(
                modifier = Modifier.size(48.dp),
                contentAlignment = Alignment.Center
            ) {
                when {
                    file.isDirectory -> {
                        Icon(
                            Icons.Default.Folder,
                            contentDescription = "文件夹",
                            modifier = Modifier.size(32.dp),
                            tint = Color(0xFFFF9800)
                        )
                    }
                    isImageFile(file.name) -> {
                        Icon(
                            Icons.Default.Image,
                            contentDescription = "图片",
                            modifier = Modifier.size(32.dp),
                            tint = Color(0xFF4CAF50)
                        )
                    }
                    isVideoFile(file.name) -> {
                        Icon(
                            Icons.Default.VideoLibrary,
                            contentDescription = "视频",
                            modifier = Modifier.size(32.dp),
                            tint = Color(0xFFE91E63)
                        )
                    }
                    isAudioFile(file.name) -> {
                        Icon(
                            Icons.Default.MusicNote,
                            contentDescription = "音频",
                            modifier = Modifier.size(32.dp),
                            tint = Color(0xFF2196F3)
                        )
                    }
                    isDocumentFile(file.name) -> {
                        Icon(
                            Icons.Default.Description,
                            contentDescription = "文档",
                            modifier = Modifier.size(32.dp),
                            tint = Color(0xFF673AB7)
                        )
                    }
                    isArchiveFile(file.name) -> {
                        Icon(
                            Icons.Default.Archive,
                            contentDescription = "压缩包",
                            modifier = Modifier.size(32.dp),
                            tint = Color(0xFF9E9E9E)
                        )
                    }
                    else -> {
                        Icon(
                            Icons.Default.InsertDriveFile,
                            contentDescription = "文件",
                            modifier = Modifier.size(32.dp),
                            tint = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
            }

            // File Info
            Column(
                modifier = Modifier.weight(1f),
                verticalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                Text(
                    text = file.name,
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Medium,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
                Text(
                    text = if (file.isDirectory) {
                        "${file.size} 项"
                    } else {
                        formatFileSize(file.size)
                    },
                    fontSize = 12.sp,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }

            // File Date
            Text(
                text = formatDate(file.lastModified),
                fontSize = 12.sp,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}

@Composable
private fun SelectionActionsBar(
    selectedCount: Int,
    onDelete: () -> Unit,
    onClearSelection: () -> Unit
) {
    Surface(
        modifier = Modifier.fillMaxWidth(),
        color = MaterialTheme.colorScheme.primaryContainer
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "已选择 $selectedCount 个文件",
                fontSize = 16.sp,
                fontWeight = FontWeight.Medium
            )

            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                TextButton(onClick = onClearSelection) {
                    Text("取消")
                }
                Button(
                    onClick = onDelete,
                    colors = ButtonDefaults.buttonColors(
                        containerColor = MaterialTheme.colorScheme.error
                    )
                ) {
                    Text("删除")
                }
            }
        }
    }
}

private fun isImageFile(fileName: String): Boolean {
    val ext = fileName.substringAfterLast('.', "").lowercase()
    return ext in listOf("jpg", "jpeg", "png", "gif", "bmp", "webp")
}

private fun isVideoFile(fileName: String): Boolean {
    val ext = fileName.substringAfterLast('.', "").lowercase()
    return ext in listOf("mp4", "avi", "mkv", "mov", "wmv", "flv", "webm")
}

private fun isAudioFile(fileName: String): Boolean {
    val ext = fileName.substringAfterLast('.', "").lowercase()
    return ext in listOf("mp3", "wav", "ogg", "m4a", "aac", "flac")
}

private fun isDocumentFile(fileName: String): Boolean {
    val ext = fileName.substringAfterLast('.', "").lowercase()
    return ext in listOf("pdf", "doc", "docx", "xls", "xlsx", "ppt", "pptx", "txt")
}

private fun isArchiveFile(fileName: String): Boolean {
    val ext = fileName.substringAfterLast('.', "").lowercase()
    return ext in listOf("zip", "rar", "7z", "tar", "gz")
}

private fun formatFileSize(size: Long): String {
    if (size < 1024) return "$size B"
    val kb = size / 1024.0
    if (kb < 1024) return String.format("%.2f KB", kb)
    val mb = kb / 1024.0
    if (mb < 1024) return String.format("%.2f MB", mb)
    val gb = mb / 1024.0
    return String.format("%.2f GB", gb)
}

private fun formatDate(timestamp: Long): String {
    val date = java.util.Date(timestamp)
    val format = java.text.SimpleDateFormat("MM-dd HH:mm", java.util.Locale.getDefault())
    return format.format(date)
}
