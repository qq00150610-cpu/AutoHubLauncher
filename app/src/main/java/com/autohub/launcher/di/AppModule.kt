package com.autohub.launcher.di

import android.content.Context
import com.autohub.launcher.data.adapter.CarAdapter
import com.autohub.launcher.data.adapter.BYDCarAdapter
import com.autohub.launcher.data.adapter.GeelyCarAdapter
import com.autohub.launcher.data.adapter.DongfengAdapter
import com.autohub.launcher.data.adapter.GenericCarAdapter
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Named
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object AppModule {

    @Provides
    @Singleton
    fun provideCarAdapter(@ApplicationContext context: Context): CarAdapter {
        // Detect car model and return appropriate adapter
        val carModel = detectCarModel(context)

        return when (carModel) {
            "BYD" -> BYDCarAdapter(context)
            "GEELY" -> GeelyCarAdapter(context)
            "DONGFENG" -> DongfengAdapter(context)
            else -> GenericCarAdapter(context)
        }
    }

    private fun detectCarModel(context: Context): String {
        // Check for specific manufacturer packages
        val packages = context.packageManager.getInstalledApplications(0)
        
        // Check for Dongfeng Aeolus launcher
        if (packages.any { it.packageName.contains("aeolus", ignoreCase = true) }) {
            return "DONGFENG"
        }
        
        // Check for BYD launcher
        if (packages.any { it.packageName.contains("byd", ignoreCase = true) }) {
            return "BYD"
        }
        
        // Check for Geely launcher
        if (packages.any { it.packageName.contains("geely", ignoreCase = true) }) {
            return "GEELY"
        }
        
        // Check build manufacturer
        val buildManufacturer = android.os.Build.MANUFACTURER
        return when {
            buildManufacturer.contains("BYD", ignoreCase = true) -> "BYD"
            buildManufacturer.contains("Geely", ignoreCase = true) -> "GEELY"
            buildManufacturer.contains("Dongfeng", ignoreCase = true) ||
            buildManufacturer.contains("Aeolus", ignoreCase = true) -> "DONGFENG"
            else -> "GENERIC"
        }
    }
}
