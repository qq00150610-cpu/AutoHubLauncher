package com.autohub.launcher.data.adapter

import android.content.Context
import android.content.Intent
import com.autohub.launcher.domain.model.CarInfo
import com.autohub.launcher.domain.model.ClimateControl
import com.autohub.launcher.domain.model.SteeringWheelKey
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject
import javax.inject.Singleton

/**
 * 吉利车型适配器
 * 支持GKUI系统
 */
@Singleton
class GeelyCarAdapter @Inject constructor(
    @ApplicationContext private val context: Context
) : CarAdapter {

    companion object {
        private const val PACKAGE_NAME_GKUI_LAUNCHER = "com.geely.gkui.launcher"
        private const val PACKAGE_NAME_GKUI_MUSIC = "com.geely.gkui.music"
        private const val PACKAGE_NAME_GKUI_VIDEO = "com.geely.gkui.video"
        private const val PACKAGE_NAME_GKUI_NAV = "com.geely.gkui.navigation"
        
        // 吉利系统设置包名
        private const val PACKAGE_NAME_GKUI_SETTINGS = "com.geely.gkui.settings"
        
        // 吉利原厂应用包名列表
        private val GEELY_SYSTEM_APPS = listOf(
            PACKAGE_NAME_GKUI_LAUNCHER,
            PACKAGE_NAME_GKUI_MUSIC,
            PACKAGE_NAME_GKUI_VIDEO,
            PACKAGE_NAME_GKUI_NAV,
            PACKAGE_NAME_GKUI_SETTINGS
        )
    }

    override val manufacturer = "吉利"
    override val supportedModels = listOf(
        "博越",
        "博越COOL",
        "博越X",
        "星越",
        "星越L",
        "缤瑞",
        "帝豪",
        "嘉际",
        "远景",
        "豪越"
    )

    override fun isCompatible(): Boolean {
        return isPackageInstalled(PACKAGE_NAME_GKUI_LAUNCHER)
    }

    override fun getCarInfo(): CarInfo {
        return CarInfo(
            manufacturer = manufacturer,
            model = detectModel(),
            systemVersion = getSystemVersion(),
            vin = getVin(),
            mileage = getMileage(),
            fuelLevel = getFuelLevel(),
            oilLevel = getOilLevel(),
            batteryLevel = getBatteryLevel()
        )
    }

    override fun openNativeSettings(): Intent {
        return Intent().apply {
            action = Intent.ACTION_MAIN
            addCategory(Intent.CATEGORY_LAUNCHER)
            setPackage(PACKAGE_NAME_GKUI_SETTINGS)
            flags = Intent.FLAG_ACTIVITY_NEW_TASK
        }
    }

    override fun openNativeMusic(): Intent {
        return Intent().apply {
            action = Intent.ACTION_MAIN
            addCategory(Intent.CATEGORY_LAUNCHER)
            setPackage(PACKAGE_NAME_GKUI_MUSIC)
            flags = Intent.FLAG_ACTIVITY_NEW_TASK
        }
    }

    override fun openNativeNavigation(): Intent {
        return Intent().apply {
            action = Intent.ACTION_MAIN
            addCategory(Intent.CATEGORY_LAUNCHER)
            setPackage(PACKAGE_NAME_GKUI_NAV)
            flags = Intent.FLAG_ACTIVITY_NEW_TASK
        }
    }

    override fun openNativeVideo(): Intent {
        return Intent().apply {
            action = Intent.ACTION_MAIN
            addCategory(Intent.CATEGORY_LAUNCHER)
            setPackage(PACKAGE_NAME_GKUI_VIDEO)
            flags = Intent.FLAG_ACTIVITY_NEW_TASK
        }
    }

    override fun getSystemApps(): List<String> {
        return GEELY_SYSTEM_APPS
    }

    override fun getSteeringWheelMapping(): Map<SteeringWheelKey, String> {
        return mapOf(
            SteeringWheelKey.VOLUME_UP to "com.autohub.launcher.music.VOLUME_UP",
            SteeringWheelKey.VOLUME_DOWN to "com.autohub.launcher.music.VOLUME_DOWN",
            SteeringWheelKey.MUTE to "com.autohub.launcher.music.MUTE",
            SteeringWheelKey.MEDIA_PLAY_PAUSE to "com.autohub.launcher.music.PLAY_PAUSE",
            SteeringWheelKey.MEDIA_PREVIOUS to "com.autohub.launcher.music.PREVIOUS",
            SteeringWheelKey.MEDIA_NEXT to "com.autohub.launcher.music.NEXT",
            SteeringWheelKey.NAV_UP to "com.autohub.launcher.navigation.UP",
            SteeringWheelKey.NAV_DOWN to "com.autohub.launcher.navigation.DOWN",
            SteeringWheelKey.NAV_LEFT to "com.autohub.launcher.navigation.LEFT",
            SteeringWheelKey.NAV_RIGHT to "com.autohub.launcher.navigation.RIGHT",
            SteeringWheelKey.VOICE_ASSISTANT to "com.autohub.launcher.voice.ASSISTANT"
        )
    }

    override fun getClimateControlCapabilities(): ClimateControl.Capabilities {
        return ClimateControl.Capabilities(
            supportsTemperature = true,
            supportsFanSpeed = true,
            supportsMode = true,
            supportsAutoMode = true,
            supportsAcd = false, // 吉利车型通常不支持
            supportsRearWindowDefogger = true,
            supportsSeatHeating = true,
            supportsSeatCooling = false,
            supportsSteeringWheelHeating = false
        )
    }

    private fun detectModel(): String {
        val buildModel = android.os.Build.MODEL
        
        return when {
            buildModel.contains("博越", ignoreCase = true) -> {
                if (buildModel.contains("COOL", ignoreCase = true)) "博越COOL"
                else if (buildModel.contains("X", ignoreCase = true)) "博越X"
                else "博越"
            }
            buildModel.contains("星越", ignoreCase = true) -> {
                if (buildModel.contains("L", ignoreCase = true)) "星越L"
                else "星越"
            }
            buildModel.contains("缤瑞", ignoreCase = true) -> "缤瑞"
            buildModel.contains("帝豪", ignoreCase = true) -> "帝豪"
            buildModel.contains("嘉际", ignoreCase = true) -> "嘉际"
            buildModel.contains("远景", ignoreCase = true) -> "远景"
            buildModel.contains("豪越", ignoreCase = true) -> "豪越"
            else -> "吉利"
        }
    }

    private fun getSystemVersion(): String {
        return try {
            val packageInfo = context.packageManager.getPackageInfo(
                PACKAGE_NAME_GKUI_LAUNCHER, 0
            )
            packageInfo.versionName ?: "Unknown"
        } catch (e: Exception) {
            "Unknown"
        }
    }

    private fun getVin(): String {
        // TODO: 通过CAN总线获取真实VIN
        return "LZHG1234567890ABCDE"
    }

    private fun getMileage(): Float {
        // TODO: 通过CAN总线获取真实里程
        return 0f
    }

    private fun getFuelLevel(): Float {
        // TODO: 通过CAN总线获取真实油量/电量
        return 0f
    }

    private fun getOilLevel(): Float {
        // TODO: 通过CAN总线获取真实机油量
        return 0f
    }

    private fun getBatteryLevel(): Float {
        // TODO: 通过CAN总线获取真实电池电量
        return 0f
    }

    private fun isPackageInstalled(packageName: String): Boolean {
        return try {
            context.packageManager.getPackageInfo(packageName, 0)
            true
        } catch (e: Exception) {
            false
        }
    }
}
