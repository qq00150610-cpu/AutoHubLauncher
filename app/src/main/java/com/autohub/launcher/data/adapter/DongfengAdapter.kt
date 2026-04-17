package com.autohub.launcher.data.adapter

import android.content.Context
import android.content.Intent
import com.autohub.launcher.domain.model.CarInfo
import com.autohub.launcher.domain.model.CarType
import com.autohub.launcher.domain.model.ClimateControl
import com.autohub.launcher.domain.model.SteeringWheelKey
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject
import javax.inject.Singleton

/**
 * 东风风神车型适配器
 * 支持车型：奕炫、奕炫GS、AX7、AX7 PRO、GS MAX等
 */
@Singleton
class DongfengAdapter @Inject constructor(
    @ApplicationContext private val context: Context
) : CarAdapter {

    companion object {
        private const val PACKAGE_NAME_AEOLUS_LAUNCHER = "com.aeolus.launcher"
        private const val PACKAGE_NAME_AEOLUS_MUSIC = "com.aeolus.music"
        private const val PACKAGE_NAME_AEOLUS_VIDEO = "com.aeolus.video"
        private const val PACKAGE_NAME_AEOLUS_NAV = "com.aeolus.navigation"
        
        // 东风风神系统设置包名
        private const val PACKAGE_NAME_AEOLUS_SETTINGS = "com.aeolus.settings"
        
        // 东风风原厂应用包名列表
        private val AEOLUS_SYSTEM_APPS = listOf(
            PACKAGE_NAME_AEOLUS_LAUNCHER,
            PACKAGE_NAME_AEOLUS_MUSIC,
            PACKAGE_NAME_AEOLUS_VIDEO,
            PACKAGE_NAME_AEOLUS_NAV,
            PACKAGE_NAME_AEOLUS_SETTINGS
        )
    }

    override val manufacturer = "东风风神"
    override val supportedModels = listOf(
        "奕炫",
        "奕炫GS",
        "AX7",
        "AX7 PRO",
        "GS MAX",
        "皓极",
        "奕炫MAX",
        "AX5"
    )

    override fun isCompatible(): Boolean {
        return isPackageInstalled(PACKAGE_NAME_AEOLUS_LAUNCHER)
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
            setPackage(PACKAGE_NAME_AEOLUS_SETTINGS)
            flags = Intent.FLAG_ACTIVITY_NEW_TASK
        }
    }

    override fun openNativeMusic(): Intent {
        return Intent().apply {
            action = Intent.ACTION_MAIN
            addCategory(Intent.CATEGORY_LAUNCHER)
            setPackage(PACKAGE_NAME_AEOLUS_MUSIC)
            flags = Intent.FLAG_ACTIVITY_NEW_TASK
        }
    }

    override fun openNativeNavigation(): Intent {
        return Intent().apply {
            action = Intent.ACTION_MAIN
            addCategory(Intent.CATEGORY_LAUNCHER)
            setPackage(PACKAGE_NAME_AEOLUS_NAV)
            flags = Intent.FLAG_ACTIVITY_NEW_TASK
        }
    }

    override fun openNativeVideo(): Intent {
        return Intent().apply {
            action = Intent.ACTION_MAIN
            addCategory(Intent.CATEGORY_LAUNCHER)
            setPackage(PACKAGE_NAME_AEOLUS_VIDEO)
            flags = Intent.FLAG_ACTIVITY_NEW_TASK
        }
    }

    override fun getSystemApps(): List<String> {
        return AEOLUS_SYSTEM_APPS
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
            SteeringWheelKey.NAV_OK to "com.autohub.launcher.navigation.OK",
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
            supportsSeatCooling = false, // 大部分车型不支持
            supportsSteeringWheelHeating = true // 2020年后车型支持
        )
    }

    /**
     * 检测具体车型
     */
    private fun detectModel(): String {
        val buildModel = android.os.Build.MODEL
        
        return when {
            buildModel.contains("AX7", ignoreCase = true) -> {
                if (buildModel.contains("PRO", ignoreCase = true)) {
                    "AX7 PRO"
                } else {
                    "AX7"
                }
            }
            buildModel.contains("GS", ignoreCase = true) -> {
                if (buildModel.contains("MAX", ignoreCase = true)) {
                    "GS MAX"
                } else {
                    "奕炫GS"
                }
            }
            buildModel.contains("奕炫", ignoreCase = true) || 
            buildModel.contains("YIXUAN", ignoreCase = true) -> {
                if (buildModel.contains("MAX", ignoreCase = true)) {
                    "奕炫MAX"
                } else {
                    "奕炫"
                }
            }
            buildModel.contains("皓极", ignoreCase = true) ||
            buildModel.contains("HAOJI", ignoreCase = true) -> "皓极"
            buildModel.contains("AX5", ignoreCase = true) -> "AX5"
            else -> "东风风神"
        }
    }

    /**
     * 获取系统版本
     */
    private fun getSystemVersion(): String {
        return try {
            val packageInfo = context.packageManager.getPackageInfo(
                PACKAGE_NAME_AEOLUS_LAUNCHER, 0
            )
            packageInfo.versionName ?: "Unknown"
        } catch (e: Exception) {
            "Unknown"
        }
    }

    /**
     * 获取车辆VIN码
     * 注意：实际需要通过CAN总线或系统API获取
     */
    private fun getVin(): String {
        // TODO: 通过CAN总线或系统API获取真实VIN
        return "LSVA1234567890ABCDE"
    }

    /**
     * 获取里程
     */
    private fun getMileage(): Float {
        // TODO: 通过CAN总线获取真实里程
        return 0f
    }

    /**
     * 获取油量/电量
     */
    private fun getFuelLevel(): Float {
        // TODO: 通过CAN总线获取真实油量/电量
        return 0f
    }

    /**
     * 获取机油量
     */
    private fun getOilLevel(): Float {
        // TODO: 通过CAN总线获取真实机油量
        return 0f
    }

    /**
     * 获取电池电量
     */
    private fun getBatteryLevel(): Float {
        // TODO: 通过CAN总线获取真实电池电量
        return 0f
    }

    /**
     * 检查包是否已安装
     */
    private fun isPackageInstalled(packageName: String): Boolean {
        return try {
            context.packageManager.getPackageInfo(packageName, 0)
            true
        } catch (e: Exception) {
            false
        }
    }

    /**
     * 获取车型特定配置
     */
    fun getModelConfig(model: String): ModelConfig {
        return when (model) {
            "奕炫", "奕炫MAX" -> ModelConfig(
                has360Camera = true,
                hasAdas = true,
                hasSmartKey = true,
                hasAutoParking = false,
                screenResolution = Pair(1920, 720),
                supportsWirelessCarPlay = model == "奕炫MAX",
                supportsWirelessAndroidAuto = model == "奕炫MAX"
            )
            "AX7", "AX7 PRO" -> ModelConfig(
                has360Camera = true,
                hasAdas = true,
                hasSmartKey = true,
                hasAutoParking = true,
                screenResolution = Pair(1920, 1080),
                supportsWirelessCarPlay = model == "AX7 PRO",
                supportsWirelessAndroidAuto = model == "AX7 PRO"
            )
            "GS MAX" -> ModelConfig(
                has360Camera = true,
                hasAdas = true,
                hasSmartKey = true,
                hasAutoParking = false,
                screenResolution = Pair(1920, 720),
                supportsWirelessCarPlay = true,
                supportsWirelessAndroidAuto = true
            )
            "皓极" -> ModelConfig(
                has360Camera = true,
                hasAdas = true,
                hasSmartKey = true,
                hasAutoParking = true,
                screenResolution = Pair(2560, 1440),
                supportsWirelessCarPlay = true,
                supportsWirelessAndroidAuto = true
            )
            else -> ModelConfig(
                has360Camera = false,
                hasAdas = false,
                hasSmartKey = false,
                hasAutoParking = false,
                screenResolution = Pair(1280, 720),
                supportsWirelessCarPlay = false,
                supportsWirelessAndroidAuto = false
            )
        }
    }

    /**
     * 车型配置数据类
     */
    data class ModelConfig(
        val has360Camera: Boolean,
        val hasAdas: Boolean,
        val hasSmartKey: Boolean,
        val hasAutoParking: Boolean,
        val screenResolution: Pair<Int, Int>,
        val supportsWirelessCarPlay: Boolean,
        val supportsWirelessAndroidAuto: Boolean
    )
}
