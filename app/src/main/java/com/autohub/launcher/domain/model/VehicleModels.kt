package com.autohub.launcher.domain.model

/**
 * 空调控制信息
 */
data class ClimateControl(
    val temperature: Float = 22f,           // 温度 (摄氏度)
    val isAcOn: Boolean = false,             // 空调是否开启
    val isAutoMode: Boolean = false,         // 自动模式
    val fanSpeed: Int = 0,                   // 风速等级 0-5
    val airMode: AirMode = AirMode.AUTO,     // 出风模式
    val isRecirculation: Boolean = false,    // 内循环
    val isDefrostFront: Boolean = false,     // 前除霜
    val isDefrostRear: Boolean = false,      // 后除霜
    val isSeatHeating: Boolean = false,      // 座椅加热
    val isSeatVentilation: Boolean = false   // 座椅通风
) {
    /**
     * 空调控制能力
     */
    data class Capabilities(
        val supportsTemperature: Boolean = false,
        val supportsFanSpeed: Boolean = false,
        val supportsMode: Boolean = false,
        val supportsAutoMode: Boolean = false,
        val supportsAcd: Boolean = false,
        val supportsRearWindowDefogger: Boolean = false,
        val supportsSeatHeating: Boolean = false,
        val supportsSeatCooling: Boolean = false,
        val supportsSteeringWheelHeating: Boolean = false
    )
}

/**
 * 出风模式
 */
enum class AirMode {
    AUTO,       // 自动
    FACE,       // 面部
    FEET,       // 脚部
    BOTH,       // 面部+脚部
    DEFROST     // 除霜
}

/**
 * 方向盘按键定义
 */
enum class SteeringWheelKey(val keyCode: Int) {
    VOLUME_UP(1),
    VOLUME_DOWN(2),
    MUTE(3),
    MEDIA_PLAY_PAUSE(4),
    MEDIA_PREVIOUS(5),
    MEDIA_NEXT(6),
    NEXT_TRACK(7),
    PREV_TRACK(8),
    VOICE_ASSISTANT(9),
    PHONE_ACCEPT(10),
    PHONE_HANGUP(11),
    PHONE(12),
    MODE(13),
    SOURCE(14),
    HOME(15),
    BACK(16),
    CRUISE(17),
    LANE_KEEP(18),
    NAV_UP(19),
    NAV_DOWN(20),
    NAV_LEFT(21),
    NAV_RIGHT(22)
}

/**
 * 车辆类型
 */
enum class CarType {
    SEDAN,      // 轿车
    SUV,        // SUV
    MPV,        // MPV
    SPORTS,     // 跑车
    PICKUP,     // 皮卡
    OTHER       // 其他
}
