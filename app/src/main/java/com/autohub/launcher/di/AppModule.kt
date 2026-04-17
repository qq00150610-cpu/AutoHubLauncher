package com.autohub.launcher.di

import android.content.Context
import com.autohub.launcher.service.CarAdapter
import com.autohub.launcher.service.BYDCarAdapter
import com.autohub.launcher.service.GeelyCarAdapter
import com.autohub.launcher.service.GenericCarAdapter
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
            "BYD" -> BYDCarAdapter()
            "GEELY" -> GeelyCarAdapter()
            else -> GenericCarAdapter()
        }
    }

    private fun detectCarModel(context: Context): String {
        // TODO: Implement car model detection
        // Check system properties, build info, etc.
        // For now, return generic
        return "GENERIC"
    }
}
