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
 */
@Singleton
class WeChatLoginService @Inject constructor(
    @ApplicationContext private val context: Context
) {
    companion object {
        // 替换为实际的微信AppID
        private const val WECHAT_APP_ID = "wx1234567890abcdef"
        private const val WECHAT_APP_SECRET = "your_app_secret"
    }

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

        val request = SendAuth.Req()
        request.scope = "snsapi_userinfo"
        request.state = generateState()
        wxApi.sendReq(request, activity)
    }

    /**
     * 处理微信登录回调
     */
    suspend fun handleLoginResult(code: Int, resp: SendAuth.Resp?): Result<WeChatLoginResponse> {
        return suspendCancellableCoroutine { continuation ->
            if (resp == null) {
                continuation.resumeWithException(Exception("微信登录响应为空"))
                return@suspendCancellableCoroutine
            }

            when (code) {
                Activity.RESULT_OK -> {
                    if (resp.errCode == SendAuth.Req.ErrCode.ERR_OK) {
                        val response = WeChatLoginResponse(
                            openId = resp.openId ?: "",
                            unionId = resp.unionId,
                            accessToken = resp.code ?: ""
                        )
                        continuation.resume(Result.success(response))
                    } else {
                        val errorMsg = when (resp.errCode) {
                            SendAuth.Req.ErrCode.ERR_AUTH_DENIED -> "用户拒绝授权"
                            SendAuth.Req.ErrCode.ERR_USER_CANCEL -> "用户取消"
                            SendAuth.Req.ErrCode.ERR_BAN -> "账号被封禁"
                            SendAuth.Req.ErrCode.ERR_COMM -> "网络错误"
                            else -> "未知错误: ${resp.errStr}"
                        }
                        continuation.resumeWithException(WeChatLoginException(errorMsg))
                    }
                }
                else -> {
                    continuation.resumeWithException(WeChatLoginException("微信登录失败"))
                }
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
