package com.autohub.launcher.service

import com.autohub.launcher.data.adapter.CarAdapter
import com.autohub.launcher.domain.model.CarInfo
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import javax.inject.Inject
import javax.inject.Singleton

/**
 * 车辆控制服务
 * 提供车辆信息获取和控制功能
 */
@Singleton
class CarControlService @Inject constructor(
    @ApplicationContext private val context: android.content.Context,
    private val carAdapter: CarAdapter
) {
    private val _carInfo = MutableStateFlow(CarInfo())
    val carInfo: StateFlow<CarInfo> = _carInfo.asStateFlow()

    fun refreshCarInfo() {
        try {
            _carInfo.value = carAdapter.getCarInfo()
        } catch (e: Exception) {
            // Handle error
        }
    }

    fun getManufacturer(): String = carAdapter.manufacturer
    fun getSupportedModels(): List<String> = carAdapter.supportedModels
}
