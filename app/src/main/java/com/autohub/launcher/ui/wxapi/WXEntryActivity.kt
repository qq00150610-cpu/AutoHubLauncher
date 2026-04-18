package com.autohub.launcher.ui.wxapi

import android.os.Bundle
import androidx.activity.ComponentActivity

/**
 * 微信登录回调Activity - 占位类
 * 微信SDK已移除，此类保留用于兼容性
 */
class WXEntryActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        finish()
    }
}
