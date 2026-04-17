package com.autohub.launcher.ui.auth

import android.app.Activity
import android.content.Intent
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.autohub.launcher.data.model.LoginResponse
import com.autohub.launcher.data.repository.UserRepository
import com.autohub.launcher.service.WeChatLoginException
import com.autohub.launcher.service.WeChatLoginService
import com.tencent.mm.opensdk.modelmsg.SendAuth
import com.tencent.mm.opensdk.openapi.IWXAPIEventHandler
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
    private val userRepository: UserRepository,
    private val weChatLoginService: WeChatLoginService
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
     * 微信登录
     */
    fun loginWithWeChat(activity: Activity) {
        viewModelScope.launch {
            _loginState.value = UiState.Loading
            try {
                // 检查是否安装微信
                if (!weChatLoginService.isWeChatInstalled()) {
                    _loginState.value = UiState.Error("未安装微信应用")
                    return@launch
                }

                // 启动微信登录
                weChatLoginService.startWeChatLogin(activity)

                // 等待回调结果（这里简化处理，实际需要在Activity中处理回调）
                // 实际使用时，需要在Activity的onActivityResult中处理微信返回的code
                // 然后调用 handleWeChatLoginResult 方法
            } catch (e: Exception) {
                _loginState.value = UiState.Error(e.message ?: "微信登录失败")
            }
        }
    }

    /**
     * 处理微信登录回调
     */
    fun handleWeChatLoginResult(code: Int, resp: SendAuth.Resp?) {
        viewModelScope.launch {
            _loginState.value = UiState.Loading
            try {
                val result = weChatLoginService.handleLoginResult(code, resp)
                if (result.isSuccess) {
                    val weChatResp = result.getOrNull()!!
                    val loginResp = userRepository.loginWithWeChat(weChatResp.accessToken)
                    if (loginResp.isSuccess) {
                        _loginState.value = UiState.Success(loginResp.getOrNull()!!)
                    } else {
                        _loginState.value = UiState.Error(loginResp.exceptionOrNull()?.message ?: "登录失败")
                    }
                } else {
                    _loginState.value = UiState.Error(result.exceptionOrNull()?.message ?: "微信登录失败")
                }
            } catch (e: Exception) {
                _loginState.value = UiState.Error(e.message ?: "微信登录失败")
            }
        }
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
