package com.autohub.launcher.ui.app

import androidx.lifecycle.ViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

/**
 * 应用全局ViewModel
 */
@HiltViewModel
class AppViewModel @Inject constructor() : ViewModel() {
    // 登录是可选功能，不强制检查
}
