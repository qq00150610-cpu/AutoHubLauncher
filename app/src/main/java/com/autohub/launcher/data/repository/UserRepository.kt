package com.autohub.launcher.data.repository

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import com.autohub.launcher.data.model.LoginResponse
import com.autohub.launcher.data.model.SmsResponse
import com.autohub.launcher.data.model.User
import com.autohub.launcher.data.model.WeChatLoginResponse
import com.autohub.launcher.data.api.AuthApi
import com.autohub.launcher.data.api.RefreshTokenRequest
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.firstOrNull
import javax.inject.Inject
import javax.inject.Singleton

private val Context.userDataStore: DataStore<Preferences> by preferencesDataStore(name = "user_prefs")

/**
 * 用户数据仓库
 */
@Singleton
class UserRepository @Inject constructor(
    @ApplicationContext private val context: Context,
    private val authApi: AuthApi
) {
    // Token存储键
    private object Keys {
        val TOKEN = stringPreferencesKey("token")
        val REFRESH_TOKEN = stringPreferencesKey("refresh_token")
        val USER_ID = stringPreferencesKey("user_id")
        val USER_PHONE = stringPreferencesKey("user_phone")
        val USER_NICKNAME = stringPreferencesKey("user_nickname")
        val USER_AVATAR = stringPreferencesKey("user_avatar")
        val WECHAT_OPENID = stringPreferencesKey("wechat_openid")
    }

    /**
     * 获取当前登录的Token
     */
    val token: Flow<String?> = context.userDataStore.data
        .map { it[Keys.TOKEN] }

    /**
     * 获取当前用户信息
     */
    val currentUser: Flow<User?> = context.userDataStore.data
        .map { prefs ->
            val id = prefs[Keys.USER_ID] ?: return@map null
            User(
                id = id,
                phone = prefs[Keys.USER_PHONE] ?: "",
                nickname = prefs[Keys.USER_NICKNAME] ?: "",
                avatar = prefs[Keys.USER_AVATAR] ?: User.DEFAULT_AVATAR,
                wechatOpenId = prefs[Keys.WECHAT_OPENID] ?: ""
            )
        }

    /**
     * 检查是否已登录
     */
    val isLoggedIn: Flow<Boolean> = token.map { !it.isNullOrEmpty() }

    /**
     * 发送短信验证码
     */
    suspend fun sendSmsCode(phone: String): Result<SmsResponse> {
        return try {
            // 模拟API调用
            val response = authApi.sendSmsCode(phone)
            Result.success(response)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    /**
     * 手机号登录/注册
     */
    suspend fun loginWithPhone(phone: String, code: String): Result<LoginResponse> {
        return try {
            val response = authApi.loginWithPhone(phone, code)
            saveUserInfo(response)
            Result.success(response)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    /**
     * 微信登录
     */
    suspend fun loginWithWeChat(authCode: String): Result<LoginResponse> {
        return try {
            val response = authApi.loginWithWeChat(authCode)
            saveUserInfo(response)
            Result.success(response)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    /**
     * 退出登录
     */
    suspend fun logout() {
        context.userDataStore.edit { prefs ->
            prefs.clear()
        }
    }

    /**
     * 刷新Token
     */
    suspend fun refreshToken(): Result<String> {
        return try {
            val refreshToken = context.userDataStore.data.map { it[Keys.REFRESH_TOKEN] }
                .firstOrNull() ?: return Result.failure(Exception("No refresh token"))

            val response = authApi.refreshToken(RefreshTokenRequest(refreshToken))
            context.userDataStore.edit { prefs ->
                prefs[Keys.TOKEN] = response.token
                prefs[Keys.REFRESH_TOKEN] = response.refreshToken
            }
            Result.success(response.token)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    /**
     * 保存用户信息到本地
     */
    private suspend fun saveUserInfo(response: LoginResponse) {
        context.userDataStore.edit { prefs ->
            prefs[Keys.TOKEN] = response.token
            prefs[Keys.REFRESH_TOKEN] = response.refreshToken
            prefs[Keys.USER_ID] = response.user.id
            prefs[Keys.USER_PHONE] = response.user.phone
            prefs[Keys.USER_NICKNAME] = response.user.nickname
            prefs[Keys.USER_AVATAR] = response.user.avatar
            prefs[Keys.WECHAT_OPENID] = response.user.wechatOpenId
        }
    }

    /**
     * 更新用户信息
     */
    suspend fun updateUserInfo(user: User) {
        context.userDataStore.edit { prefs ->
            prefs[Keys.USER_NICKNAME] = user.nickname
            prefs[Keys.USER_AVATAR] = user.avatar
        }
    }
}
