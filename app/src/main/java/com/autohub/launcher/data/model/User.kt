package com.autohub.launcher.data.model

import com.google.gson.annotations.SerializedName

/**
 * 用户数据模型
 */
data class User(
    @SerializedName("id")
    val id: String = "",
    @SerializedName("phone")
    val phone: String = "",
    @SerializedName("nickname")
    val nickname: String = "",
    @SerializedName("avatar")
    val avatar: String = "",
    @SerializedName("wechatOpenId")
    val wechatOpenId: String = "",
    @SerializedName("createTime")
    val createTime: Long = 0L,
    @SerializedName("lastLoginTime")
    val lastLoginTime: Long = 0L,
    @SerializedName("isVip")
    val isVip: Boolean = false,
    @SerializedName("vipExpireTime")
    val vipExpireTime: Long = 0L
) {
    companion object {
        const val DEFAULT_AVATAR = "https://api.dicebear.com/7.x/avataaars/svg?seed=user"
    }
}

/**
 * 登录响应数据
 */
data class LoginResponse(
    @SerializedName("user")
    val user: User,
    @SerializedName("token")
    val token: String,
    @SerializedName("refreshToken")
    val refreshToken: String
)

/**
 * 微信登录响应
 */
data class WeChatLoginResponse(
    @SerializedName("openId")
    val openId: String,
    @SerializedName("unionId")
    val unionId: String? = null,
    @SerializedName("accessToken")
    val accessToken: String
)

/**
 * 验证码响应
 */
data class SmsResponse(
    @SerializedName("success")
    val success: Boolean,
    @SerializedName("message")
    val message: String,
    @SerializedName("expireTime")
    val expireTime: Int = 60  // 验证码有效期（秒）
)
