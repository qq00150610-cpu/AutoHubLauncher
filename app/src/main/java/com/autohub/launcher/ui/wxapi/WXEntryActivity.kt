package com.autohub.launcher.ui.wxapi

import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.viewModels
import com.autohub.launcher.ui.auth.LoginViewModel
import com.tencent.mm.opensdk.modelmsg.SendAuth
import com.tencent.mm.opensdk.openapi.IWXAPIEventHandler
import com.tencent.mm.opensdk.openapi.WXAPIFactory
import dagger.hilt.android.AndroidEntryPoint

/**
 * 微信登录回调Activity
 */
@AndroidEntryPoint
class WXEntryActivity : ComponentActivity(), IWXAPIEventHandler {

    private lateinit var api: com.tencent.mm.opensdk.openapi.IWXAPI
    
    private val loginViewModel: LoginViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // 初始化微信API
        api = WXAPIFactory.createWXAPI(this, "wx1234567890abcdef", false)
        api.handleIntent(intent, this)
    }

    override fun onReq(baseReq: com.tencent.mm.opensdk.modelbase.BaseReq) {
        // 微信向第三方应用发起请求，处理请求
        finish()
    }

    override fun onResp(resp: com.tencent.mm.opensdk.modelbase.BaseResp) {
        when (resp.type) {
            1 -> { // 登录响应
                val authResp = resp as SendAuth.Resp
                loginViewModel.handleWeChatLoginResult(authResp)
            }
            else -> {
                // 其他响应
            }
        }
        finish()
    }

    override fun onNewIntent(intent: Intent?) {
        super.onNewIntent(intent)
        setIntent(intent)
        api.handleIntent(intent, this)
    }
}
