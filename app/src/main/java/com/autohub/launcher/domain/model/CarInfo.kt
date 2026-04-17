package com.autohub.launcher.domain.model

data class CarInfo(
    val vin: String? = null,
    val model: String? = null,
    val manufacturer: String? = null,
    val systemVersion: String? = null,
    val mileage: Float = 0f,
    val fuelLevel: Float = 0f,
    val oilLevel: Float = 0f,
    val batteryLevel: Float = 0f,
    val speed: Float = 0f,
    val engineTemp: Float? = null,
    val tirePressure: List<Float>? = null,
    val isEngineRunning: Boolean = false,
    val doorStatus: Map<String, Boolean> = emptyMap(),
    val windowStatus: Map<String, Boolean> = emptyMap(),
    val lastUpdate: Long = System.currentTimeMillis()
)
