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
 * 比亚迪车型适配器
 * 支持DiLink系统
 */
@Singleton
class BYDCarAdapter @Inject constructor(
    @ApplicationContext private val context: Context
) : CarAdapter {

    companion object {
        private const val PACKAGE_NAME_DILINK_LAUNCHER = "com.bycd.launcher"
        private const val PACKAGE_NAME_DILINK_MUSIC = "com.bycd.music"
        private const val PACKAGE_NAME_DILINK_VIDEO = "com.bycd.video"
        private const val PACKAGE_NAME_DILINK_NAV = "com.bycd.navigation"
        
        // 比亚迪系统设置包名
        private const val PACKAGE_NAME_DILINK_SETTINGS = "com.bycd.settings"
        
        // 比亚迪原厂应用包名列表
        private val BYD_SYSTEM_APPS = listOf(
            PACKAGE_NAME_DILINK_LAUNCHER,
            PACKAGE_NAME_DILINK_MUSIC,
            PACKAGE_NAME_DILINK_VIDEO,
            PACKAGE_NAME_DILINK_NAV,
            PACKAGE_NAME_DILINK_SETTINGS
        )
    }

    override val manufacturer = "比亚迪"
    override val supportedModels = listOf(
        "秦PLUS",
        "汉EV",
        "汉DM",
        "唐EV",
        "宋PLUS",
        "元PLUS",
        "海豚",
        "海豹",
        "宋Pro DM-i"
    )

    override fun isCompatible(): Boolean {
        return isPackageInstalled(PACKAGE_NAME_DILINK_LAUNCHER)
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
            setPackage(PACKAGE_NAME_DILINK_SETTINGS)
            flags = Intent.FLAG_ACTIVITY_NEW_TASK
        }
    }

    override fun openNativeMusic(): Intent {
        return Intent().apply {
            action = Intent.ACTION_MAIN
            addCategory(Intent.CATEGORY_LAUNCHER)
            setPackage(PACKAGE_NAME_DILINK_MUSIC)
            flags = Intent.FLAG_ACTIVITY_NEW_TASK
        }
    }

    override fun openNativeNavigation(): Intent {
        return Intent().apply {
            action = Intent.ACTION_MAIN
            addCategory(Intent.CATEGORY_LAUNCHER)
            setPackage(PACKAGE_NAME_DILINK_NAV)
            flags = Intent.FLAG_ACTIVITY_NEW_TASK
        }
    }

    override fun openNativeVideo(): Intent {
        return Intent().apply {
            action = Intent.ACTION_MAIN
            addCategory(Intent.CATEGORY_LAUNCHER)
            setPackage(PACKAGE_NAME_DILINK_VIDEO)
            flags = Intent.FLAG_ACTIVITY_NEW_TASK
        }
    }

    override fun getSystemApps(): List<String> {
        return BYD_SYSTEM_APPS
    }

    override fun getSteeringWheelMapping(): Map<SteeringWheelKey, String> {
        return mapOf(
            SteeringWheelKey.VOLUME_UP to "com.autohub.launcher.music.VOLUME_UP",
            SteeringWheelKey.VOLUME_DOWN to "com.autohub.launcher.music.VOLUME_DOWN",
            SteeringWheelKey.MUTE to "com.autohub.launcher.music.MUTE",
            SteeringWheelKey.MEDIA_PLAY_PAUSE to "com.autohub.launcher.music.PLAY_PAUSE",
            SteeringWheelKey.MEDIA_PREVIOUS to "com.autohub.launcher.music.PREVIOUS",
            SteeringWheelKey.MEDIA_NEXT to "com.autohub.launcher.music.NEXT",
            SteeringWheelKey.VOICE_ASSISTANT to "com.autohub.launcher.voice.ASSISTANT",
            SteeringWheelKey.PHONE_ACCEPT to "com.autohub.launcher.phone.ACCEPT",
            SteeringWheelKey.PHONE_HANGUP to "com.autohub.launcher.phone.HANGUP"
        )
    }

    override fun getClimateControlCapabilities(): ClimateControl.Capabilities {
        return ClimateControl.Capabilities(
            supportsTemperature = true,
            supportsFanSpeed = true,
            supportsMode = true,
            supportsAutoMode = true,
            supportsAcd = true,
            supportsRearWindowDefogger = true,
            supportsSeatHeating = true,
            supportsSeatCooling = true,
            supportsSteeringWheelHeating = true
        )
    }

    private fun detectModel(): String {
        val buildModel = android.os.Build.MODEL
        
        return when {
            buildModel.contains("秦", ignoreCase = true) -> {
                if (buildModel.contains("PLUS", ignoreCase = true)) "秦PLUS" else "秦"
            }
            buildModel.contains("汉", ignoreCase = true) -> {
                if (buildModel.contains("DM", ignoreCase = true)) "汉DM" else "汉EV"
            }
            buildModel.contains("唐", ignoreCase = true) -> "唐EV"
            buildModel.contains("宋", ignoreCase = true) -> {
                if (buildModel.contains("PLUS", ignoreCase = true)) "宋PLUS"
                else if (buildModel.contains("Pro", ignoreCase = true)) "宋Pro DM-i"
                else "宋"
            }
            buildModel.contains("元", ignoreCase = true) -> "元PLUS"
            buildModel.contains("海豚", ignoreCase = true) -> "海豚"
            buildModel.contains("海豹", ignoreCase = true) -> "海豹"
            else -> "比亚迪"
        }
    }

    private fun getSystemVersion(): String {
        return try {
            val packageInfo = context.packageManager.getPackageInfo(
                PACKAGE_NAME_DILINK_LAUNCHER, 0
            )
            packageInfo.versionName ?: "Unknown"
        } catch (e: Exception) {
            "Unknown"
        }
    }

    private fun getVin(): String {
        // TODO: 通过CAN总线获取真实VIN
        return "LGXC1234567890ABCDE"
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
