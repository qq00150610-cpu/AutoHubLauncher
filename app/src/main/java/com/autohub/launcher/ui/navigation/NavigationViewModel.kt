package com.autohub.launcher.ui.navigation

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.autohub.launcher.domain.usecase.GetInstalledAppsUseCase
import com.autohub.launcher.service.NavigationService
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class NavigationViewModel @Inject constructor(
    private val getInstalledAppsUseCase: GetInstalledAppsUseCase,
    private val navigationService: NavigationService
) : ViewModel() {

    private val _uiState = MutableStateFlow(NavigationUiState())
    val uiState: StateFlow<NavigationUiState> = _uiState.asStateFlow()

    init {
        loadNavigationApps()
    }

    private fun loadNavigationApps() {
        viewModelScope.launch {
            val allApps = getInstalledAppsUseCase()
            allApps.collect { apps ->
                val navApps = apps.filter { app ->
                    app.packageName.contains("autonavi") ||
                    app.packageName.contains("baidu") ||
                    app.packageName.contains("tencent") ||
                    app.packageName.contains("map") ||
                    app.packageName.contains("nav")
                }

                _uiState.value = _uiState.value.copy(
                    navigationApps = navApps
                )
            }
        }
    }

    fun startNavigation(destination: String, appPackageName: String?) {
        navigationService.startNavigation(destination)
        _uiState.value = _uiState.value.copy(
            isNavigating = true,
            currentDestination = destination
        )
    }

    fun stopNavigation() {
        _uiState.value = _uiState.value.copy(
            isNavigating = false,
            currentDestination = null
        )
    }

    fun onDestinationChanged(destination: String) {
        _uiState.value = _uiState.value.copy(
            searchQuery = destination
        )
    }

    fun clearSearch() {
        _uiState.value = _uiState.value.copy(
            searchQuery = ""
        )
    }
}

data class NavigationUiState(
    val navigationApps: List<com.autohub.launcher.domain.model.AppInfo> = emptyList(),
    val isNavigating: Boolean = false,
    val currentDestination: String? = null,
    val searchQuery: String = "",
    val recentDestinations: List<String> = emptyList(),
    val favoriteDestinations: List<String> = emptyList()
)
