package com.autohub.launcher.data.adapter

import android.content.Intent
import com.autohub.launcher.domain.model.CarInfo
import com.autohub.launcher.domain.model.ClimateControl
import com.autohub.launcher.domain.model.SteeringWheelKey

/**
 * 车辆适配器接口
 * 用于适配不同品牌车型的特殊功能
 */
interface CarAdapter {

    /**
     * 制造商名称
     */
    val manufacturer: String

    /**
     * 支持的车型列表
     */
    val supportedModels: List<String>

    /**
     * 检查是否兼容当前设备
     */
    fun isCompatible(): Boolean

    /**
     * 获取车辆信息
     */
    fun getCarInfo(): CarInfo

    /**
     * 打开原生设置
     */
    fun openNativeSettings(): Intent

    /**
     * 打开原生音乐
     */
    fun openNativeMusic(): Intent

    /**
     * 打开原生导航
     */
    fun openNativeNavigation(): Intent

    /**
     * 打开原生视频
     */
    fun openNativeVideo(): Intent

    /**
     * 获取系统应用包名列表
     */
    fun getSystemApps(): List<String>

    /**
     * 获取方向盘按键映射
     */
    fun getSteeringWheelMapping(): Map<SteeringWheelKey, String>

    /**
     * 获取空调控制能力
     */
    fun getClimateControlCapabilities(): ClimateControl.Capabilities
}
