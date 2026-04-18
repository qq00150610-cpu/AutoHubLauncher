package com.autohub.launcher.ui.auth

import android.app.Activity
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.autohub.launcher.data.model.LoginResponse
import com.autohub.launcher.data.repository.UserRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

/**
 * 登录界面ViewModel
 */
@HiltViewModel
class LoginViewModel @Inject constructor(
    private val userRepository: UserRepository
) : ViewModel() {

    private val _loginState = MutableStateFlow<UiState<LoginResponse>>(UiState.Idle)
    val loginState: StateFlow<UiState<LoginResponse>> = _loginState.asStateFlow()

    private val _codeState = MutableStateFlow<UiState<Boolean>>(UiState.Idle)
    val codeState: StateFlow<UiState<Boolean>> = _codeState.asStateFlow()

    private val _countdown = MutableStateFlow(60)
    val countdown: StateFlow<Int> = _countdown.asStateFlow()

    /**
     * 发送短信验证码
     */
    fun sendSmsCode(phone: String) {
        viewModelScope.launch {
            _codeState.value = UiState.Loading
            try {
                val result = userRepository.sendSmsCode(phone)
                if (result.isSuccess) {
                    _codeState.value = UiState.Success(true)
                    startCountdown()
                } else {
                    _codeState.value = UiState.Error(result.exceptionOrNull()?.message ?: "发送失败")
                }
            } catch (e: Exception) {
                _codeState.value = UiState.Error(e.message ?: "发送失败")
            }
        }
    }

    /**
     * 手机号登录
     */
    fun loginWithPhone(phone: String, code: String) {
        viewModelScope.launch {
            _loginState.value = UiState.Loading
            try {
                val result = userRepository.loginWithPhone(phone, code)
                if (result.isSuccess) {
                    _loginState.value = UiState.Success(result.getOrNull()!!)
                } else {
                    _loginState.value = UiState.Error(result.exceptionOrNull()?.message ?: "登录失败")
                }
            } catch (e: Exception) {
                _loginState.value = UiState.Error(e.message ?: "登录失败")
            }
        }
    }

    /**
     * 手机号注册
     */
    fun registerWithPhone(phone: String, code: String) {
        viewModelScope.launch {
            _loginState.value = UiState.Loading
            try {
                val result = userRepository.loginWithPhone(phone, code)  // 注册和登录使用同一个接口
                if (result.isSuccess) {
                    _loginState.value = UiState.Success(result.getOrNull()!!)
                } else {
                    _loginState.value = UiState.Error(result.exceptionOrNull()?.message ?: "注册失败")
                }
            } catch (e: Exception) {
                _loginState.value = UiState.Error(e.message ?: "注册失败")
            }
        }
    }

    /**
     * 微信登录 - 暂不可用
     */
    fun loginWithWeChat(activity: Activity) {
        _loginState.value = UiState.Error("微信登录暂不可用")
    }

    /**
     * 开始倒计时
     */
    private fun startCountdown() {
        viewModelScope.launch {
            for (i in 60 downTo 1) {
                _countdown.value = i
                kotlinx.coroutines.delay(1000)
            }
            _countdown.value = 60
        }
    }
}

/**
 * UI状态封装
 */
sealed class UiState<out T> {
    object Idle : UiState<Nothing>()
    object Loading : UiState<Nothing>()
    data class Success<out T>(val data: T) : UiState<T>()
    data class Error(val message: String) : UiState<Nothing>()
}
