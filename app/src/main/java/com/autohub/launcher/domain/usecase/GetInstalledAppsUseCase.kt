package com.autohub.launcher.domain.usecase

import android.content.Context
import android.content.pm.ApplicationInfo
import android.content.pm.PackageManager
import com.autohub.launcher.domain.model.AppInfo
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import java.io.File
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class GetInstalledAppsUseCase @Inject constructor(
    @ApplicationContext private val context: Context
) {
    operator fun invoke(): Flow<List<AppInfo>> = flow {
        val packageManager = context.packageManager
        val packages = packageManager.getInstalledApplications(PackageManager.GET_META_DATA)

        val appList = packages
            .filter { it.flags and ApplicationInfo.FLAG_SYSTEM == 0 } // Filter out system apps
            .map { appInfo ->
                try {
                    val appLabel = packageManager.getApplicationLabel(appInfo).toString()
                    val apkPath = appInfo.sourceDir
                    val apkFile = File(apkPath)
                    val size = if (apkFile.exists()) apkFile.length() else 0L

                    AppInfo(
                        packageName = appInfo.packageName,
                        name = appLabel,
                        versionName = packageManager.getPackageInfo(appInfo.packageName, 0).versionName,
                        versionCode = packageManager.getPackageInfo(appInfo.packageName, 0).longVersionCode,
                        isSystemApp = (appInfo.flags and ApplicationInfo.FLAG_SYSTEM) != 0,
                        firstInstallTime = packageManager.getPackageInfo(appInfo.packageName, 0).firstInstallTime,
                        lastUpdateTime = packageManager.getPackageInfo(appInfo.packageName, 0).lastUpdateTime,
                        size = size
                    )
                } catch (e: PackageManager.NameNotFoundException) {
                    null
                }
            }
            .filterNotNull()
            .sortedBy { it.name }

        emit(appList)
    }
}
