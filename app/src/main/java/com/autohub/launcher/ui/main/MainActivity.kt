package com.autohub.launcher.ui.main

import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.provider.Settings
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.ui.Modifier
import com.autohub.launcher.ui.app.AppScreen
import com.autohub.launcher.ui.app.setFinishActivityCallback
import com.autohub.launcher.ui.theme.AutoHubTheme
import com.autohub.launcher.service.FloatingBallService
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : ComponentActivity() {

    private val viewModel: MainViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // 设置Activity结束回调
        setFinishActivityCallback {
            finish()
        }

        setContent {
            AutoHubTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    AppScreen()
                }
            }
        }

        // Start floating ball service only if permission granted
        startFloatingBallServiceIfPermitted()
    }

    private fun startFloatingBallServiceIfPermitted() {
        // 检查悬浮窗权限
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M && Settings.canDrawOverlays(this)) {
            startFloatingBallService()
        }
        // 如果没有权限，暂时不启动悬浮球服务，等待用户授权
    }

    private fun startFloatingBallService() {
        try {
            val intent = Intent(this, FloatingBallService::class.java)
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                startForegroundService(intent)
            } else {
                startService(intent)
            }
        } catch (e: Exception) {
            // 忽略启动失败，不影响主界面
        }
    }

    override fun onResume() {
        super.onResume()
        viewModel.onResume()
    }

    override fun onPause() {
        super.onPause()
        viewModel.onPause()
    }
}
