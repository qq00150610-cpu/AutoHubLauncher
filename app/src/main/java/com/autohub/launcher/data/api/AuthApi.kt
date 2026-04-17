package com.autohub.launcher.data.api

import com.autohub.launcher.data.model.LoginResponse
import com.autohub.launcher.data.model.SmsResponse
import retrofit2.Response
import retrofit2.http.*

/**
 * 认证API接口
 */
interface AuthApi {

    /**
     * 发送短信验证码
     */
    @POST("api/auth/sms/send")
    suspend fun sendSmsCode(
        @Query("phone") phone: String
    ): SmsResponse

    /**
     * 手机号登录/注册
     */
    @POST("api/auth/login/phone")
    suspend fun loginWithPhone(
        @Body request: PhoneLoginRequest
    ): LoginResponse

    /**
     * 微信登录
     */
    @POST("api/auth/login/wechat")
    suspend fun loginWithWeChat(
        @Body request: WeChatLoginRequest
    ): LoginResponse

    /**
     * 刷新Token
     */
    @POST("api/auth/refresh")
    suspend fun refreshToken(
        @Body request: RefreshTokenRequest
    ): LoginResponse

    /**
     * 获取用户信息
     */
    @GET("api/user/info")
    suspend fun getUserInfo(
        @Header("Authorization") token: String
    ): com.autohub.launcher.data.model.User
}

/**
 * 手机号登录请求
 */
data class PhoneLoginRequest(
    val phone: String,
    val code: String
)

/**
 * 微信登录请求
 */
data class WeChatLoginRequest(
    val code: String,
    val deviceId: String = ""
)

/**
 * 刷新Token请求
 */
data class RefreshTokenRequest(
    val refreshToken: String
)
