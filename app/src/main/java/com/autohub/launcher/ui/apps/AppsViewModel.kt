package com.autohub.launcher.ui.apps

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.autohub.launcher.data.local.AppManager
import com.autohub.launcher.domain.model.AppInfo
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class AppsViewModel @Inject constructor(
    private val appManager: AppManager
) : ViewModel() {

    private val _uiState = MutableStateFlow(AppsUiState())
    val uiState: StateFlow<AppsUiState> = _uiState.asStateFlow()

    init {
        loadApps()
    }

    private fun loadApps() {
        viewModelScope.launch {
            appManager.allApps.collect { apps ->
                _uiState.value = _uiState.value.copy(
                    allApps = apps,
                    filteredApps = apps,
                    systemApps = appManager.getSystemApps(),
                    userApps = appManager.getUserApps()
                )
            }
        }
    }

    fun onSearchQueryChanged(query: String) {
        val filtered = if (query.isBlank()) {
            _uiState.value.allApps
        } else {
            appManager.searchApps(query)
        }
        _uiState.value = _uiState.value.copy(
            searchQuery = query,
            filteredApps = filtered
        )
    }

    fun onFilterChanged(filter: AppFilter) {
        val filtered = when (filter) {
            AppFilter.ALL -> _uiState.value.allApps
            AppFilter.SYSTEM -> _uiState.value.systemApps
            AppFilter.USER -> _uiState.value.userApps
        }

        _uiState.value = _uiState.value.copy(
            currentFilter = filter,
            filteredApps = filtered
        )
    }

    fun onSortChanged(sortBy: AppSortBy) {
        val sorted = when (sortBy) {
            AppSortBy.NAME -> _uiState.value.filteredApps.sortedBy { it.appName }
            AppSortBy.SIZE -> _uiState.value.filteredApps.sortedByDescending { it.size }
            AppSortBy.INSTALL_TIME -> _uiState.value.filteredApps.sortedByDescending { it.installTime }
            AppSortBy.UPDATE_TIME -> _uiState.value.filteredApps.sortedByDescending { it.updateTime }
        }

        _uiState.value = _uiState.value.copy(
            currentSort = sortBy,
            filteredApps = sorted
        )
    }

    fun onAppSelected(packageName: String) {
        appManager.launchApp(packageName)
    }

    fun onUninstallApp(packageName: String) {
        appManager.uninstallApp(packageName)
        // Reload apps after uninstall
        loadApps()
    }

    fun onClearCache(packageName: String) {
        appManager.clearAppCache(packageName)
    }

    fun onClearData(packageName: String) {
        appManager.clearAppData(packageName)
    }
}

data class AppsUiState(
    val allApps: List<com.autohub.launcher.data.local.AppDetails> = emptyList(),
    val systemApps: List<com.autohub.launcher.data.local.AppDetails> = emptyList(),
    val userApps: List<com.autohub.launcher.data.local.AppDetails> = emptyList(),
    val filteredApps: List<com.autohub.launcher.data.local.AppDetails> = emptyList(),
    val searchQuery: String = "",
    val currentFilter: AppFilter = AppFilter.ALL,
    val currentSort: AppSortBy = AppSortBy.NAME,
    val isLoading: Boolean = false
)

enum class AppFilter {
    ALL, SYSTEM, USER
}

enum class AppSortBy {
    NAME, SIZE, INSTALL_TIME, UPDATE_TIME
}
