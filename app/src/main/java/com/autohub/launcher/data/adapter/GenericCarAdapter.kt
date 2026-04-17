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
 * 通用车型适配器
 * 用于适配不支持的品牌车型，提供基础功能
 */
@Singleton
class GenericCarAdapter @Inject constructor(
    @ApplicationContext private val context: Context
) : CarAdapter {

    override val manufacturer = "通用"
    override val supportedModels = listOf("通用车型")

    override fun isCompatible(): Boolean {
        // 通用适配器始终兼容，作为默认选项
        return true
    }

    override fun getCarInfo(): CarInfo {
        return CarInfo(
            manufacturer = manufacturer,
            model = android.os.Build.MODEL,
            systemVersion = "Android ${android.os.Build.VERSION.RELEASE}",
            vin = "UNKNOWN",
            mileage = 0f,
            fuelLevel = 0f,
            oilLevel = 0f,
            batteryLevel = 0f
        )
    }

    override fun openNativeSettings(): Intent {
        return Intent(android.provider.Settings.ACTION_SETTINGS).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK
        }
    }

    override fun openNativeMusic(): Intent {
        return Intent().apply {
            action = Intent.ACTION_VIEW
            type = "audio/*"
            flags = Intent.FLAG_ACTIVITY_NEW_TASK
        }
    }

    override fun openNativeNavigation(): Intent {
        return Intent(Intent.ACTION_VIEW).apply {
            data = android.net.Uri.parse("geo:0,0?q=")
            flags = Intent.FLAG_ACTIVITY_NEW_TASK
        }
    }

    override fun openNativeVideo(): Intent {
        return Intent(Intent.ACTION_VIEW).apply {
            type = "video/*"
            flags = Intent.FLAG_ACTIVITY_NEW_TASK
        }
    }

    override fun getSystemApps(): List<String> {
        // 返回空列表，通用适配器不知道具体的系统应用
        return emptyList()
    }

    override fun getSteeringWheelMapping(): Map<SteeringWheelKey, String> {
        return mapOf(
            SteeringWheelKey.VOLUME_UP to "com.autohub.launcher.music.VOLUME_UP",
            SteeringWheelKey.VOLUME_DOWN to "com.autohub.launcher.music.VOLUME_DOWN",
            SteeringWheelKey.MUTE to "com.autohub.launcher.music.MUTE",
            SteeringWheelKey.MEDIA_PLAY_PAUSE to "com.autohub.launcher.music.PLAY_PAUSE",
            SteeringWheelKey.MEDIA_PREVIOUS to "com.autohub.launcher.music.PREVIOUS",
            SteeringWheelKey.MEDIA_NEXT to "com.autohub.launcher.music.NEXT"
        )
    }

    override fun getClimateControlCapabilities(): ClimateControl.Capabilities {
        // 通用适配器提供基础空调控制能力
        return ClimateControl.Capabilities(
            supportsTemperature = true,
            supportsFanSpeed = true,
            supportsMode = true,
            supportsAutoMode = true,
            supportsAcd = false,
            supportsRearWindowDefogger = true,
            supportsSeatHeating = false,
            supportsSeatCooling = false,
            supportsSteeringWheelHeating = false
        )
    }
}
