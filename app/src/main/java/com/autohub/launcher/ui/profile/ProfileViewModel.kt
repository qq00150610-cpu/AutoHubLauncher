package com.autohub.launcher.ui.profile

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.autohub.launcher.data.repository.UserRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

/**
 * 个人中心ViewModel
 */
@HiltViewModel
class ProfileViewModel @Inject constructor(
    private val userRepository: UserRepository
) : ViewModel() {

    /**
     * 当前用户信息
     */
    val currentUser = userRepository.currentUser

    /**
     * 是否已登录
     */
    val isLoggedIn = userRepository.isLoggedIn

    /**
     * 显示编辑资料对话框
     */
    fun showEditProfile() {
        // TODO: 实现编辑资料功能
    }

    /**
     * 更新用户信息
     */
    fun updateUserInfo(nickname: String, avatar: String) {
        viewModelScope.launch {
            try {
                val user = userRepository.currentUser.firstOrNull() ?: return@launch
                val updatedUser = user.copy(
                    nickname = nickname,
                    avatar = avatar
                )
                userRepository.updateUserInfo(updatedUser)
            } catch (e: Exception) {
                // 处理错误
            }
        }
    }

    /**
     * 退出登录
     */
    fun logout() {
        viewModelScope.launch {
            userRepository.logout()
        }
    }
}
