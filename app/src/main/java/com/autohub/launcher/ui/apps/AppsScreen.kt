package com.autohub.launcher.ui.apps

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.lazy.items
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

@Composable
fun AppsScreen(
    viewModel: AppsViewModel = hiltViewModel(),
    onBack: () -> Unit = {}
) {
    val uiState by viewModel.uiState.collectAsState()

    Scaffold(
        modifier = Modifier.fillMaxSize(),
        topBar = {
            AppsTopBar(
                onBack = onBack,
                onSearch = { viewModel.onSearchQueryChanged(it) }
            )
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .padding(paddingValues)
                .fillMaxSize(),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // Filter Chips
            FilterChipsRow(
                currentFilter = uiState.currentFilter,
                onFilterChanged = { viewModel.onFilterChanged(it) }
            )

            // Sort Options
            SortOptionsRow(
                currentSort = uiState.currentSort,
                onSortChanged = { viewModel.onSortChanged(it) }
            )

            // Apps Grid
            LazyVerticalGrid(
                columns = GridCells.Fixed(4),
                modifier = Modifier.fillMaxSize(),
                contentPadding = PaddingValues(16.dp),
                horizontalArrangement = Arrangement.spacedBy(16.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                items(uiState.filteredApps) { app ->
                    AppGridItem(
                        app = app,
                        onAppClick = { viewModel.onAppSelected(app.packageName) },
                        onAppLongClick = { /* Show app details */ }
                    )
                }
            }
        }
    }
}

@Composable
private fun AppsTopBar(
    onBack: () -> Unit,
    onSearch: (String) -> Unit
) {
    var searchQuery by androidx.compose.runtime.remember { androidx.compose.runtime.mutableStateOf("") }

    TopAppBar(
        title = { 
            if (searchQuery.isBlank()) {
                Text("应用管理", fontWeight = FontWeight.Bold)
            } else {
                Text("搜索: $searchQuery", fontWeight = FontWeight.Bold)
            }
        },
        navigationIcon = {
            IconButton(onClick = onBack) {
                Icon(Icons.Default.ArrowBack, contentDescription = "返回")
            }
        },
        actions = {
            if (searchQuery.isBlank()) {
                IconButton(onClick = { /* Toggle view mode */ }) {
                    Icon(Icons.Default.GridView, contentDescription = "切换视图")
                }
            }
        },
        colors = TopAppBarDefaults.topAppBarColors(
            containerColor = MaterialTheme.colorScheme.surface
        )
    )
}

@Composable
private fun FilterChipsRow(
    currentFilter: AppFilter,
    onFilterChanged: (AppFilter) -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp),
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        FilterChip(
            selected = currentFilter == AppFilter.ALL,
            onClick = { onFilterChanged(AppFilter.ALL) },
            label = { Text("全部") },
            leadingIcon = if (currentFilter == AppFilter.ALL) {
                { Icon(Icons.Default.Done, contentDescription = null) }
            } else null
        )

        FilterChip(
            selected = currentFilter == AppFilter.USER,
            onClick = { onFilterChanged(AppFilter.USER) },
            label = { Text("用户应用") },
            leadingIcon = if (currentFilter == AppFilter.USER) {
                { Icon(Icons.Default.Done, contentDescription = null) }
            } else null
        )

        FilterChip(
            selected = currentFilter == AppFilter.SYSTEM,
            onClick = { onFilterChanged(AppFilter.SYSTEM) },
            label = { Text("系统应用") },
            leadingIcon = if (currentFilter == AppFilter.SYSTEM) {
                { Icon(Icons.Default.Done, contentDescription = null) }
            } else null
        )
    }
}

@Composable
private fun SortOptionsRow(
    currentSort: AppSortBy,
    onSortChanged: (AppSortBy) -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp),
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        AssistChip(
            onClick = { onSortChanged(AppSortBy.NAME) },
            label = { Text("名称") },
            border = if (currentSort == AppSortBy.NAME) {
                BorderStroke(2.dp, MaterialTheme.colorScheme.primary)
            } else {
                null
            }
        )

        AssistChip(
            onClick = { onSortChanged(AppSortBy.SIZE) },
            label = { Text("大小") },
            border = if (currentSort == AppSortBy.SIZE) {
                BorderStroke(2.dp, MaterialTheme.colorScheme.primary)
            } else {
                null
            }
        )

        AssistChip(
            onClick = { onSortChanged(AppSortBy.INSTALL_TIME) },
            label = { Text("安装时间") },
            border = if (currentSort == AppSortBy.INSTALL_TIME) {
                BorderStroke(2.dp, MaterialTheme.colorScheme.primary)
            } else {
                null
            }
        )
    }
}

@Composable
private fun AppGridItem(
    app: com.autohub.launcher.data.local.AppDetails,
    onAppClick: () -> Unit,
    onAppLongClick: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(
                onClick = onAppClick,
                onLongClick = onAppLongClick
            ),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        // App Icon
        Surface(
            modifier = Modifier.size(64.dp),
            shape = RoundedCornerShape(12.dp),
            color = MaterialTheme.colorScheme.primaryContainer
        ) {
            Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = app.appName.first().toString(),
                    fontSize = 32.sp,
                    color = MaterialTheme.colorScheme.primary,
                    fontWeight = FontWeight.Bold
                )
            }
        }

        // App Name
        Text(
            text = app.appName,
            fontSize = 12.sp,
            maxLines = 1,
            fontWeight = FontWeight.Medium
        )

        // App Size
        Text(
            text = formatFileSize(app.size),
            fontSize = 10.sp,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
    }
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

@Composable
private fun AppListItem(
    app: com.autohub.launcher.data.local.AppDetails,
    onAppClick: () -> Unit,
    onAppLongClick: () -> Unit
) {
    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(
                onClick = onAppClick,
                onLongClick = onAppLongClick
            ),
        shape = RoundedCornerShape(12.dp),
        color = MaterialTheme.colorScheme.surfaceVariant
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            horizontalArrangement = Arrangement.spacedBy(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // App Icon
            Surface(
                modifier = Modifier.size(56.dp),
                shape = RoundedCornerShape(12.dp),
                color = MaterialTheme.colorScheme.primaryContainer
            ) {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = app.appName.first().toString(),
                        fontSize = 24.sp,
                        color = MaterialTheme.colorScheme.primary,
                        fontWeight = FontWeight.Bold
                    )
                }
            }

            // App Info
            Column(
                modifier = Modifier.weight(1f),
                verticalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                Text(
                    text = app.appName,
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Medium
                )
                Text(
                    text = "v${app.versionName}",
                    fontSize = 12.sp,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                Text(
                    text = formatFileSize(app.size),
                    fontSize = 12.sp,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }

            // Options
            IconButton(onClick = { /* Show options */ }) {
                Icon(
                    Icons.Default.MoreVert,
                    contentDescription = "更多选项"
                )
            }
        }
    }
}
