package com.autohub.launcher.domain.model

data class WeatherInfo(
    val temperature: Int,
    val condition: String,
    val location: String,
    val humidity: Int? = null,
    val windSpeed: Int? = null,
    val timestamp: Long = System.currentTimeMillis()
)

enum class WeatherCondition(val displayName: String, val icon: String) {
    SUNNY("晴", "☀️"),
    CLOUDY("多云", "☁️"),
    RAINY("雨", "🌧️"),
    SNOWY("雪", "❄️"),
    THUNDERSTORM("雷暴", "⛈️"),
    FOGGY("雾", "🌫️"),
    WINDY("大风", "💨")
}
