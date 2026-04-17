package com.autohub.launcher.data.model

import kotlinx.serialization.Serializable

/**
 * 用户数据模型
 */
@Serializable
data class User(
    val id: String = "",
    val phone: String = "",
    val nickname: String = "",
    val avatar: String = "",
    val wechatOpenId: String = "",
    val createTime: Long = 0L,
    val lastLoginTime: Long = 0L,
    val isVip: Boolean = false,
    val vipExpireTime: Long = 0L
) {
    companion object {
        const val DEFAULT_AVATAR = "https://api.dicebear.com/7.x/avataaars/svg?seed=user"
    }
}

/**
 * 登录响应数据
 */
@Serializable
data class LoginResponse(
    val user: User,
    val token: String,
    val refreshToken: String
)

/**
 * 微信登录响应
 */
@Serializable
data class WeChatLoginResponse(
    val openId: String,
    val unionId: String? = null,
    val accessToken: String
)

/**
 * 验证码响应
 */
@Serializable
data class SmsResponse(
    val success: Boolean,
    val message: String,
    val expireTime: Int = 60  // 验证码有效期（秒）
)
