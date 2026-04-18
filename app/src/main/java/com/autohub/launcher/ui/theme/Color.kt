package com.autohub.launcher.ui.theme

import androidx.compose.ui.graphics.Color

// ==================== 新 UI 配色方案 ====================

// 背景渐变 - 蓝紫色调
val BgGradientStart = Color(0xFF1a1a2e)  // 深蓝
val BgGradientEnd = Color(0xFF16213e)    // 深紫蓝

// 弹窗背景 - 半透明深色
val CardBackground = Color(0xCC1a1a2e)   // 半透明深色
val CardBackgroundLight = Color(0x80FFFFFF)  // 半透明白

// 强调色
val AccentBlue = Color(0xFF00d4ff)       // 青蓝色
val AccentOrange = Color(0xFFff6b35)     // 橙色
val AccentGreen = Color(0xFF00ff88)      // 青绿色

// 文字颜色
val TextPrimary = Color.White
val TextSecondary = Color(0xFF9e9e9e)    // 灰色
val TextAccent = Color(0xFF00ff88)       // 青绿色

// 底部栏
val BottomBarBackground = Color(0xFF1a1a2e)  // 深色底栏
val BottomBarIcon = Color(0x99FFFFFF)     // 半透明白

// ==================== 原有 HiCar 配色保留 ====================

// HiCar 风格深色主题配色
// 主背景色 - 深蓝渐变
val HiCarBackgroundDark = Color(0xFF0A1628)        // 深夜蓝
val HiCarBackgroundSecondary = Color(0xFF152238)  // 次级深蓝
val HiCarBackgroundCard = Color(0xFF1A2A40)       // 卡片深蓝

// 毛玻璃效果
val HiCarGlassBackground = Color(0x40FFFFFF)     // 白色半透明
val HiCarGlassBorder = Color(0x30FFFFFF)         // 边框半透明白色

// 强调色 - 蓝色系
val HiCarPrimary = Color(0xFF2196F3)              // 主蓝色
val HiCarPrimaryDark = Color(0xFF1976D2)           // 深蓝
val HiCarPrimaryLight = Color(0xFF64B5F6)         // 浅蓝
val HiCarAccent = Color(0xFF00BCD4)               // 青色强调
val HiCarAccentOrange = Color(0xFFFF6D00)          // 橙色（导航）

// 状态色
val HiCarSuccess = Color(0xFF4CAF50)              // 成功绿
val HiCarWarning = Color(0xFFFFC107)              // 警告黄
val HiCarError = Color(0xFFF44336)               // 错误红

// 文字色
val HiCarTextPrimary = Color(0xFFFFFFFF)          // 主文字白
val HiCarTextSecondary = Color(0xB3FFFFFF)        // 次级文字（70%白）
val HiCarTextHint = Color(0x80FFFFFF)             // 提示文字（50%白）
val HiCarTextAccent = Color(0xFF64B5F6)          // 强调文字

// 渐变色定义
val HiCarGradientStart = Color(0xFF0D2137)       // 渐变起始
val HiCarGradientEnd = Color(0xFF1A3A5C)          // 渐变结束

// 卡片渐变
val HiCarCardGradientStart = Color(0xFF1E3A5F)    // 卡片渐变起始
val HiCarCardGradientEnd = Color(0xFF152238)     // 卡片渐变结束

// DOCK 栏颜色
val HiCarDockBackground = Color(0xE6121E2E)       // DOCK 栏背景
val HiCarDockItemActive = Color(0xFF2196F3)      // DOCK 激活项
val HiCarDockItemInactive = Color(0x99FFFFFF)     // DOCK 非激活项

// 导航卡片特殊色
val HiCarNavCardGradient = listOf(
    Color(0xFF1E4D78),
    Color(0xFF0D2840)
)

// 音乐卡片特殊色
val HiCarMusicCardGradient = listOf(
    Color(0xFF5C2D91),
    Color(0xFF2D1B55)
)

// 兼容旧代码
val PrimaryBlue = HiCarPrimary
val PrimaryBlueDark = HiCarPrimaryDark
val PrimaryBlueLight = HiCarPrimaryLight

val SecondaryPink = Color(0xFFE91E63)
val SecondaryPinkDark = Color(0xFFC2185B)
val SecondaryPinkLight = Color(0xFFF8BBD9)

val SuccessGreen = HiCarSuccess
val WarningOrange = HiCarWarning
val ErrorRed = HiCarError

val BackgroundLight = Color(0xFFFAFAFA)
val BackgroundDark = HiCarBackgroundDark

val SurfaceLight = Color(0xFFFFFFFF)
val SurfaceDark = HiCarBackgroundCard

val TextPrimaryCompat = HiCarTextPrimary
val TextSecondaryCompat = HiCarTextSecondary
val TextHintCompat = HiCarTextHint
val TextOnPrimary = Color(0xFFFFFFFF)
