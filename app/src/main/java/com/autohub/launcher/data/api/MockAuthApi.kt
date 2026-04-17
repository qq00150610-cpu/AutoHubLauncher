package com.autohub.launcher.data.api

import com.autohub.launcher.data.model.LoginResponse
import com.autohub.launcher.data.model.SmsResponse
import com.autohub.launcher.data.model.User
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.*

/**
 * Mock认证API实现（用于演示）
 */
class MockAuthApi : AuthApi {

    private val mockUsers = mutableMapOf<String, User>()
    private val mockCodes = mutableMapOf<String, String>()

    init {
        // 初始化一个测试用户
        mockUsers["13800138000"] = User(
            id = "user_001",
            phone = "13800138000",
            nickname = "测试用户",
            avatar = User.DEFAULT_AVATAR,
            createTime = System.currentTimeMillis(),
            lastLoginTime = System.currentTimeMillis()
        )
    }

    override suspend fun sendSmsCode(phone: String): SmsResponse {
        // 模拟发送验证码（固定为123456）
        val code = "123456"
        mockCodes[phone] = code
        return SmsResponse(
            success = true,
            message = "验证码已发送",
            expireTime = 60
        )
    }

    override suspend fun loginWithPhone(request: PhoneLoginRequest): LoginResponse {
        val code = mockCodes[request.phone]
        if (code == null || code != request.code) {
            throw Exception("验证码错误或已过期")
        }

        val user = mockUsers.getOrPut(request.phone) {
            User(
                id = "user_${System.currentTimeMillis()}",
                phone = request.phone,
                nickname = "用户${request.phone.substring(7)}",
                avatar = User.DEFAULT_AVATAR,
                createTime = System.currentTimeMillis(),
                lastLoginTime = System.currentTimeMillis()
            )
        }

        // 更新最后登录时间
        val updatedUser = user.copy(lastLoginTime = System.currentTimeMillis())
        mockUsers[request.phone] = updatedUser

        return LoginResponse(
            user = updatedUser,
            token = "token_${System.currentTimeMillis()}",
            refreshToken = "refresh_${System.currentTimeMillis()}"
        )
    }

    override suspend fun loginWithWeChat(request: WeChatLoginRequest): LoginResponse {
        // 模拟微信登录
        val user = User(
            id = "user_${System.currentTimeMillis()}",
            phone = "",
            nickname = "微信用户",
            avatar = User.DEFAULT_AVATAR,
            wechatOpenId = "wx_${System.currentTimeMillis()}",
            createTime = System.currentTimeMillis(),
            lastLoginTime = System.currentTimeMillis()
        )

        return LoginResponse(
            user = user,
            token = "token_${System.currentTimeMillis()}",
            refreshToken = "refresh_${System.currentTimeMillis()}"
        )
    }

    override suspend fun refreshToken(request: RefreshTokenRequest): LoginResponse {
        // 模拟刷新token
        return LoginResponse(
            user = User(
                id = "user_001",
                phone = "13800138000",
                nickname = "测试用户",
                avatar = User.DEFAULT_AVATAR
            ),
            token = "new_token_${System.currentTimeMillis()}",
            refreshToken = "new_refresh_${System.currentTimeMillis()}"
        )
    }

    override suspend fun getUserInfo(token: String): User {
        // 模拟获取用户信息
        return User(
            id = "user_001",
            phone = "13800138000",
            nickname = "测试用户",
            avatar = User.DEFAULT_AVATAR
        )
    }
}

/**
 * 创建Mock API实例
 */
fun createMockAuthApi(): AuthApi {
    return MockAuthApi()
}
