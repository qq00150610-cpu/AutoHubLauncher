package com.autohub.launcher.data.local

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.*
import androidx.datastore.preferences.preferencesDataStore
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject
import javax.inject.Singleton

private val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "settings")

@Singleton
class SettingsRepository @Inject constructor(
    @ApplicationContext private val context: Context
) {

    private object PreferencesKeys {
        val FLOATING_BALL_ENABLED = booleanPreferencesKey("floating_ball_enabled")
        val AUTO_HIDE_FLOATING_BALL = booleanPreferencesKey("auto_hide_floating_ball")
        val DARK_MODE = booleanPreferencesKey("dark_mode")
        val CAR_MODEL = stringPreferencesKey("car_model")
        val FAVORITE_APPS = stringPreferencesKey("favorite_apps")
        val WIDGET_CONFIG = stringPreferencesKey("widget_config")
    }

    // Floating Ball Settings
    val floatingBallEnabled: Flow<Boolean> = context.dataStore.data.map { preferences ->
        preferences[PreferencesKeys.FLOATING_BALL_ENABLED] ?: true
    }

    suspend fun setFloatingBallEnabled(enabled: Boolean) {
        context.dataStore.edit { preferences ->
            preferences[PreferencesKeys.FLOATING_BALL_ENABLED] = enabled
        }
    }

    val autoHideFloatingBall: Flow<Boolean> = context.dataStore.data.map { preferences ->
        preferences[PreferencesKeys.AUTO_HIDE_FLOATING_BALL] ?: true
    }

    suspend fun setAutoHideFloatingBall(enabled: Boolean) {
        context.dataStore.edit { preferences ->
            preferences[PreferencesKeys.AUTO_HIDE_FLOATING_BALL] = enabled
        }
    }

    // Theme Settings
    val darkMode: Flow<Boolean> = context.dataStore.data.map { preferences ->
        preferences[PreferencesKeys.DARK_MODE] ?: false
    }

    suspend fun setDarkMode(enabled: Boolean) {
        context.dataStore.edit { preferences ->
            preferences[PreferencesKeys.DARK_MODE] = enabled
        }
    }

    // Car Settings
    val carModel: Flow<String> = context.dataStore.data.map { preferences ->
        preferences[PreferencesKeys.CAR_MODEL] ?: "GENERIC"
    }

    suspend fun setCarModel(model: String) {
        context.dataStore.edit { preferences ->
            preferences[PreferencesKeys.CAR_MODEL] = model
        }
    }

    // App Settings
    val favoriteApps: Flow<String> = context.dataStore.data.map { preferences ->
        preferences[PreferencesKeys.FAVORITE_APPS] ?: ""
    }

    suspend fun setFavoriteApps(apps: String) {
        context.dataStore.edit { preferences ->
            preferences[PreferencesKeys.FAVORITE_APPS] = apps
        }
    }

    // Widget Settings
    val widgetConfig: Flow<String> = context.dataStore.data.map { preferences ->
        preferences[PreferencesKeys.WIDGET_CONFIG] ?: ""
    }

    suspend fun setWidgetConfig(config: String) {
        context.dataStore.edit { preferences ->
            preferences[PreferencesKeys.WIDGET_CONFIG] = config
        }
    }
}
