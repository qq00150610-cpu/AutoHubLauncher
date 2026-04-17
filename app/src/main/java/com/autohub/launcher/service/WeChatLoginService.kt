package com.autohub.launcher.service

import android.app.Activity
import android.content.Context
import com.autohub.launcher.data.model.WeChatLoginResponse
import com.tencent.mm.opensdk.modelmsg.SendAuth
import com.tencent.mm.opensdk.openapi.IWXAPI
import com.tencent.mm.opensdk.openapi.IWXAPIEventHandler
import com.tencent.mm.opensdk.openapi.WXAPIFactory
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.suspendCancellableCoroutine
import javax.inject.Inject
import javax.inject.Singleton
import kotlin.coroutines.resume
import kotlin.coroutines.resumeWithException

/**
 * 微信登录服务
 * 注意：微信SDK集成需要配置微信开放平台AppID
 * 当前为简化实现，仅提供框架代码
 */
@Singleton
class WeChatLoginService @Inject constructor(
    @ApplicationContext private val context: Context
) {
    companion object {
        // TODO: 替换为实际的微信开放平台AppID
        private const val WECHAT_APP_ID = "wx1234567890abcdef"
    }

    // IWXAPI 实例
    private val wxApi: IWXAPI by lazy {
        WXAPIFactory.createWXAPI(context, WECHAT_APP_ID, true).apply {
            registerApp(WECHAT_APP_ID)
        }
    }

    /**
     * 检查微信是否已安装
     */
    fun isWeChatInstalled(): Boolean {
        return wxApi.isWXAppInstalled
    }

    /**
     * 启动微信登录
     */
    fun startWeChatLogin(activity: Activity) {
        if (!isWeChatInstalled()) {
            throw WeChatNotInstalledException()
        }

        val request = SendAuth.Req().apply {
            scope = "snsapi_userinfo"
            state = generateState()
        }
        wxApi.sendReq(request)
    }

    /**
     * 处理微信登录回调
     */
    suspend fun handleLoginResult(resp: SendAuth.Resp): Result<WeChatLoginResponse> {
        return suspendCancellableCoroutine { continuation ->
            if (resp.errCode == 0) { // ErrCode.ERR_OK
                val response = WeChatLoginResponse(
                    openId = resp.openId ?: "",
                    unionId = null,
                    accessToken = resp.code ?: ""
                )
                continuation.resume(Result.success(response))
            } else {
                val errorMsg = when (resp.errCode) {
                    -1 -> "微信签名配置错误"
                    -2 -> "用户取消"
                    else -> "微信登录失败: ${resp.errStr}"
                }
                continuation.resumeWithException(WeChatLoginException(errorMsg))
            }
        }
    }

    /**
     * 生成随机State参数
     */
    private fun generateState(): String {
        val chars = "abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789"
        return (1..32).map { chars.random() }.joinToString("")
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
