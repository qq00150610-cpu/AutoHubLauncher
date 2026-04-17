package com.autohub.launcher.ui.theme

import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp

/**
 * 屏幕适配工具类
 * 支持多种屏幕分辨率：手机、平板、车机横屏等
 */
data class ScreenDimensions(
    val widthPx: Int,
    val heightPx: Int,
    val widthDp: Dp,
    val heightDp: Dp,
    val density: Float,
    val isLandscape: Boolean,
    val screenWidthType: ScreenWidthType
) {
    /**
     * 根据基准宽度(假设基准为 360dp 手机竖屏 或 720dp 车机横屏)计算适配后的尺寸
     */
    fun scaledSize(baseSize: Dp, baseWidth: Dp = if (isLandscape) 720.dp else 360.dp): Dp {
        val scaleFactor = widthDp / baseWidth
        return (baseSize.value * scaleFactor).dp
    }
    
    /**
     * 根据屏幕类型返回合适的字体缩放
     */
    val fontScale: Float
        get() = when (screenWidthType) {
            ScreenWidthType.COMPACT -> 1.0f
            ScreenWidthType.MEDIUM -> 1.1f
            ScreenWidthType.EXPANDED -> 1.2f
        }
    
    /**
     * 获取适配后的内边距
     */
    val contentPadding: Dp
        get() = when {
            widthDp < 360.dp -> 12.dp
            widthDp < 600.dp -> 16.dp
            widthDp < 840.dp -> 24.dp
            else -> 32.dp
        }
    
    /**
     * 获取适配后的卡片圆角
     */
    val cardCornerRadius: Dp
        get() = when {
            widthDp < 360.dp -> 8.dp
            widthDp < 600.dp -> 12.dp
            else -> 16.dp
        }
    
    /**
     * 获取适配后的图标大小
     */
    val iconSize: Dp
        get() = when {
            widthDp < 360.dp -> 20.dp
            widthDp < 600.dp -> 24.dp
            else -> 32.dp
        }
    
    /**
     * 获取适配后的大图标大小
     */
    val largeIconSize: Dp
        get() = when {
            widthDp < 360.dp -> 36.dp
            widthDp < 600.dp -> 48.dp
            else -> 64.dp
        }
    
    /**
     * 网格列数
     */
    val gridColumns: Int
        get() = when {
            widthDp < 360.dp -> 2
            widthDp < 480.dp -> 3
            widthDp < 600.dp -> 4
            widthDp < 840.dp -> 5
            else -> 6
        }
}

enum class ScreenWidthType {
    COMPACT,   // < 600dp (手机)
    MEDIUM,    // 600dp - 840dp (小平板/大屏手机)
    EXPANDED   // > 840dp (平板/车机横屏)
}

/**
 * 获取当前屏幕尺寸信息
 */
@Composable
fun rememberScreenDimensions(): ScreenDimensions {
    val configuration = LocalConfiguration.current
    
    return remember(configuration) {
        val widthDp = configuration.screenWidthDp.dp
        val heightDp = configuration.screenHeightDp.dp
        
        ScreenDimensions(
            widthPx = configuration.screenWidthDp,
            heightPx = configuration.screenHeightDp,
            widthDp = widthDp,
            heightDp = heightDp,
            density = configuration.densityDpi / 160f,
            isLandscape = configuration.screenWidthDp > configuration.screenHeightDp,
            screenWidthType = when {
                configuration.screenWidthDp < 600 -> ScreenWidthType.COMPACT
                configuration.screenWidthDp < 840 -> ScreenWidthType.MEDIUM
                else -> ScreenWidthType.EXPANDED
            }
        )
    }
}

/**
 * 扩展函数：将固定dp值转换为适配后的dp值
 */
@Composable
fun Dp.adapted(): Dp {
    val screenDims = rememberScreenDimensions()
    return screenDims.scaledSize(this)
}

/**
 * 预定义的适配尺寸
 */
object AdaptiveSizes {
    // 小尺寸元素
    val smallPadding: Dp @Composable get() = rememberScreenDimensions().run { 
        scaledSize(8.dp) 
    }
    
    // 标准内边距
    val standardPadding: Dp @Composable get() = rememberScreenDimensions().contentPadding
    
    // 卡片内边距
    val cardPadding: Dp @Composable get() = rememberScreenDimensions().run { 
        scaledSize(12.dp) 
    }
    
    // 列表项高度
    val listItemHeight: Dp @Composable get() = rememberScreenDimensions().run { 
        scaledSize(56.dp) 
    }
    
    // 按钮高度
    val buttonHeight: Dp @Composable get() = rememberScreenDimensions().run { 
        scaledSize(48.dp) 
    }
    
    // 标题字体大小
    val titleFontSize: Dp @Composable get() = rememberScreenDimensions().run { 
        scaledSize(20.dp) 
    }
    
    // 正文体大小
    val bodyFontSize: Dp @Composable get() = rememberScreenDimensions().run { 
        scaledSize(14.dp) 
    }
}
