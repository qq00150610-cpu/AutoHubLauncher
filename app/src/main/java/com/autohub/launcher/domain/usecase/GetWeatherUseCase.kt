package com.autohub.launcher.domain.usecase

import com.autohub.launcher.domain.model.WeatherInfo
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class GetWeatherUseCase @Inject constructor() {
    operator fun invoke(): Flow<WeatherInfo> = flow {
        // TODO: Implement actual weather API call
        // For now, return mock data
        emit(
            WeatherInfo(
                temperature = 25,
                condition = "晴",
                location = "深圳·南山区",
                humidity = 65,
                windSpeed = 12
            )
        )
    }
}
