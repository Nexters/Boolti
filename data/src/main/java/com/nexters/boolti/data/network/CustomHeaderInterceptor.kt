package com.nexters.boolti.data.network

import android.app.ActivityManager
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.os.BatteryManager
import android.os.Build
import com.nexters.boolti.data.BuildConfig
import dagger.hilt.android.qualifiers.ApplicationContext
import okhttp3.Interceptor
import okhttp3.Response
import java.util.Locale
import java.util.TimeZone
import javax.inject.Inject

internal class CustomHeaderInterceptor @Inject constructor(
    @ApplicationContext private val context: Context
) : Interceptor {

    // 앱 실행 중에 바뀌지 않는 값은 한 번만 계산한다
    private val appVersion: String by lazy { readAppVersion() }
    private val os = "Android ${Build.VERSION.RELEASE} (SDK ${Build.VERSION.SDK_INT})"
    private val buildType = if (BuildConfig.DEBUG) "debug" else "release"

    override fun intercept(chain: Interceptor.Chain): Response {
        val request = chain.request().newBuilder()
            .addHeader("X-BOOLTI-App-Version", appVersion)
            .addHeader("X-BOOLTI-Device-Model", Build.MODEL)
            .addHeader("X-BOOLTI-OS", os)
            .addHeader("X-BOOLTI-Locale", Locale.getDefault().toString())
            .addHeader("X-BOOLTI-Timezone", TimeZone.getDefault().id)
            .addHeader("X-BOOLTI-Screen", getScreenInfo())
            .addHeader("X-BOOLTI-Build-Type", buildType)
            .addHeader("X-BOOLTI-Storage-Free", getStorageFree())
            .addHeader("X-BOOLTI-Memory", getMemoryInfo())
            .addHeader("X-BOOLTI-Battery", getBatteryInfo())
            .build()

        return chain.proceed(request)
    }

    private fun readAppVersion(): String {
        return try {
            val packageInfo = context.packageManager.getPackageInfo(context.packageName, 0)
            packageInfo.versionName ?: "unknown"
        } catch (e: Exception) {
            "unknown"
        }
    }

    private fun getScreenInfo(): String {
        return try {
            val displayMetrics = context.resources.displayMetrics
            "${displayMetrics.widthPixels}x${displayMetrics.heightPixels} / ${displayMetrics.densityDpi}dpi"
        } catch (e: Exception) {
            "unknown"
        }
    }

    private fun getStorageFree(): String {
        return try {
            val internalDir = context.filesDir
            val freeBytes = internalDir.freeSpace
            val freeGB = freeBytes / (1024 * 1024 * 1024)
            val freeMB = (freeBytes / (1024 * 1024)) % 1024
            "${freeGB}.${freeMB}GB"
        } catch (e: Exception) {
            "unknown"
        }
    }

    private fun getMemoryInfo(): String {
        return try {
            val activityManager =
                context.getSystemService(Context.ACTIVITY_SERVICE) as ActivityManager
            val memoryInfo = ActivityManager.MemoryInfo()
            activityManager.getMemoryInfo(memoryInfo)

            val availMB = memoryInfo.availMem / (1024 * 1024)
            val totalMB = memoryInfo.totalMem / (1024 * 1024)
            "avail=${availMB}MB / total=${totalMB}MB"
        } catch (e: Exception) {
            "unknown"
        }
    }

    private fun getBatteryInfo(): String {
        return try {
            val batteryIntent = context.registerReceiver(
                null,
                IntentFilter(Intent.ACTION_BATTERY_CHANGED)
            )
            val level = batteryIntent?.getIntExtra(BatteryManager.EXTRA_LEVEL, -1) ?: -1
            val scale = batteryIntent?.getIntExtra(BatteryManager.EXTRA_SCALE, -1) ?: -1
            val status =
                batteryIntent?.getIntExtra(BatteryManager.EXTRA_STATUS, -1) ?: -1

            val batteryPercent = if (level >= 0 && scale > 0) {
                (level * 100 / scale)
            } else {
                -1
            }

            val statusText = when (status) {
                BatteryManager.BATTERY_STATUS_CHARGING -> "charging"
                BatteryManager.BATTERY_STATUS_DISCHARGING -> "discharging"
                BatteryManager.BATTERY_STATUS_FULL -> "full"
                BatteryManager.BATTERY_STATUS_NOT_CHARGING -> "not charging"
                else -> "unknown"
            }

            if (batteryPercent >= 0) "$statusText $batteryPercent%" else "unknown"
        } catch (e: Exception) {
            "unknown"
        }
    }
}
