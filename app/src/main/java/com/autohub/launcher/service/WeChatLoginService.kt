package com.autohub.launcher.service

import android.app.Activity
import android.content.Context
import com.autohub.launcher.data.model.WeChatLoginResponse
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject
import javax.inject.Singleton

/**
 * 微信登录服务 - 简化版本
 * 注意：微信SDK已移除，微信登录功能暂时禁用
 */
@Singleton
class WeChatLoginService @Inject constructor(
    @ApplicationContext private val context: Context
) {
    /**
     * 检查微信是否已安装 - 始终返回false
     */
    fun isWeChatInstalled(): Boolean = false

    /**
     * 启动微信登录 - 抛出异常
     */
    fun startWeChatLogin(activity: Activity) {
        throw WeChatNotInstalledException()
    }

    /**
     * 处理微信登录回调
     */
    suspend fun handleLoginResult(resp: Any): Result<WeChatLoginResponse> {
        return Result.failure(WeChatLoginException("微信登录暂不可用"))
    }
}

/**
 * 微信未安装异常
 */
class WeChatNotInstalledException : Exception("未安装微信应用，请先安装微信")

/**
 * 微信登录异常
 */
class WeChatLoginException(message: String) : Exception(message)
