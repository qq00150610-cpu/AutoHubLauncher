package com.autohub.launcher.ui.theme

import android.app.Activity
import android.os.Build
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.SideEffect
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.platform.LocalView
import androidx.core.view.WindowCompat

private val HiCarDarkColorScheme = darkColorScheme(
    primary = HiCarPrimary,
    secondary = HiCarAccent,
    tertiary = HiCarAccentOrange,
    background = HiCarBackgroundDark,
    surface = HiCarBackgroundCard,
    surfaceVariant = HiCarBackgroundSecondary,
    onPrimary = HiCarTextPrimary,
    onSecondary = HiCarTextPrimary,
    onTertiary = HiCarTextPrimary,
    onBackground = HiCarTextPrimary,
    onSurface = HiCarTextPrimary,
    onSurfaceVariant = HiCarTextSecondary,
)

@Composable
fun AutoHubTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    dynamicColor: Boolean = false,
    content: @Composable () -> Unit
) {
    // 始终使用深色主题（车载风格）
    val colorScheme = HiCarDarkColorScheme
    
    val view = LocalView.current
    if (!view.isInEditMode) {
        SideEffect {
            val window = (view.context as Activity).window
            window.statusBarColor = HiCarBackgroundDark.toArgb()
            WindowCompat.getInsetsController(window, view).isAppearanceLightStatusBars = false
        }
    }

    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography,
        content = content
    )
}
