package com.autohub.launcher.ui.app

import androidx.compose.runtime.*
import androidx.hilt.navigation.compose.hiltViewModel
import com.autohub.launcher.ui.auth.LoginScreen
import com.autohub.launcher.ui.main.MainScreen
import com.autohub.launcher.ui.main.MainViewModel
import com.autohub.launcher.ui.profile.ProfileScreen

/**
 * 应用主界面
 * 直接进入主界面，登录是可选功能（用于解锁收费主题）
 */
@Composable
fun AppScreen(
    appViewModel: AppViewModel = hiltViewModel()
) {
    var showProfile by remember { mutableStateOf(false) }
    var showLogin by remember { mutableStateOf(false) }

    when {
        showLogin -> {
            LoginScreen(
                onLoginSuccess = {
                    showLogin = false
                },
                onBackClick = {
                    showLogin = false
                }
            )
        }
        showProfile -> {
            ProfileScreen(
                onLogout = {
                    showProfile = false
                    showLogin = true
                },
                onBackClick = {
                    showProfile = false
                }
            )
        }
        else -> {
            MainScreenContainer(
                onNavigateToProfile = {
                    showProfile = true
                },
                onNavigateToLogin = {
                    showLogin = true
                }
            )
        }
    }
}

/**
 * MainScreen 容器
 * 直接使用 MainViewModel 来处理所有导航逻辑
 */
@Composable
private fun MainScreenContainer(
    onNavigateToProfile: () -> Unit,
    onNavigateToLogin: () -> Unit
) {
    val mainViewModel: MainViewModel = hiltViewModel()
    
    MainScreen(
        viewModel = mainViewModel,
        onNavigateToSettings = { mainViewModel.openSettings() },
        onNavigateToNavigation = { mainViewModel.openNavigationApp() },
        onNavigateToMusic = { mainViewModel.openMusicApp() },
        onNavigateToVideo = { mainViewModel.openVideoApp() },
        onNavigateToProfile = onNavigateToProfile
    )
}

/**
 * 结束Activity的回调
 */
private var finishActivityCallback: (() -> Unit)? = null

/**
 * 设置结束Activity的回调
 */
fun setFinishActivityCallback(callback: () -> Unit) {
    finishActivityCallback = callback
}

/**
 * 结束Activity
 */
private fun finishActivity() {
    finishActivityCallback?.invoke()
}
