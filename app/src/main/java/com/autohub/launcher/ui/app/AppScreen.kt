package com.autohub.launcher.ui.app

import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.hilt.navigation.compose.hiltViewModel
import com.autohub.launcher.ui.auth.LoginScreen
import com.autohub.launcher.ui.main.MainScreen
import com.autohub.launcher.ui.main.MainViewModel
import com.autohub.launcher.ui.profile.ProfileScreen

/**
 * 应用主界面
 * 管理登录状态和页面导航
 */
@Composable
fun AppScreen(
    appViewModel: AppViewModel = hiltViewModel()
) {
    val isLoggedIn by appViewModel.isLoggedIn.collectAsState(initial = false)
    var showLogin by remember { mutableStateOf(!isLoggedIn) }
    var showProfile by remember { mutableStateOf(false) }

    LaunchedEffect(isLoggedIn) {
        showLogin = !isLoggedIn
    }

    when {
        showLogin -> {
            LoginScreen(
                onLoginSuccess = {
                    showLogin = false
                },
                onBackClick = {
                    finishActivity()
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
            // 只在需要时创建 MainViewModel
            MainScreenContainer(
                onNavigateToProfile = {
                    showProfile = true
                }
            )
        }
    }
}

/**
 * MainScreen 容器，延迟创建 ViewModel
 */
@Composable
private fun MainScreenContainer(
    onNavigateToProfile: () -> Unit
) {
    val mainViewModel: MainViewModel = hiltViewModel()
    
    MainScreen(
        viewModel = mainViewModel,
        onNavigateToSettings = { /* Navigate to settings */ },
        onNavigateToNavigation = { /* Navigate to navigation */ },
        onNavigateToMusic = { /* Navigate to music */ },
        onNavigateToVideo = { /* Navigate to video */ },
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
