package com.autohub.launcher.domain.usecase

import android.app.ActivityManager
import android.content.Context
import com.autohub.launcher.domain.model.AppInfo
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class GetBackgroundAppsUseCase @Inject constructor(
    @ApplicationContext private val context: Context
) {
    operator fun invoke(): Flow<List<AppInfo>> = flow {
        val activityManager = context.getSystemService(Context.ACTIVITY_SERVICE) as ActivityManager
        val runningTasks = activityManager.getRunningTasks(10)

        val backgroundApps = runningTasks
            .mapNotNull { task ->
                task.topActivity?.let { activityInfo ->
                    try {
                        val appInfo = context.packageManager.getApplicationInfo(activityInfo.packageName, 0)
                        val appName = context.packageManager.getApplicationLabel(appInfo).toString()
                        AppInfo(
                            packageName = activityInfo.packageName,
                            name = appName
                        )
                    } catch (e: Exception) {
                        AppInfo(
                            packageName = activityInfo.packageName,
                            name = activityInfo.packageName
                        )
                    }
                }
            }
            .distinctBy { it.packageName }
            .take(5) // Limit to 5 apps

        emit(backgroundApps)
    }
}
