package com.autohub.launcher.ui.app

import androidx.lifecycle.ViewModel
import com.autohub.launcher.data.repository.UserRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import javax.inject.Inject

/**
 * 应用全局ViewModel
 */
@HiltViewModel
class AppViewModel @Inject constructor(
    userRepository: UserRepository
) : ViewModel() {

    /**
     * 用户登录状态
     */
    val isLoggedIn: StateFlow<Boolean> = userRepository.isLoggedIn.asStateFlow()
}
