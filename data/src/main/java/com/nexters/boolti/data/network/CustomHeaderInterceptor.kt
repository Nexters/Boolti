package com.nexters.boolti.data.network

import android.content.Context
import android.os.Build
import com.nexters.boolti.data.BuildConfig
import dagger.hilt.android.qualifiers.ApplicationContext
import okhttp3.Interceptor
import okhttp3.Response
import java.util.Locale
import java.util.TimeZone
import java.util.UUID
import javax.inject.Inject

internal class CustomHeaderInterceptor @Inject constructor(
    @ApplicationContext private val context: Context
) : Interceptor {

    private val sessionId: String
        get() = UUID.randomUUID().toString().take(12)

    override fun intercept(chain: Interceptor.Chain): Response {
        val request = chain.request().newBuilder()
            .addHeader("X-BOOLTI-App-Version", getAppVersion())
            .addHeader("X-BOOLTI-Device-Model", Build.MODEL)
            .addHeader(
                "X-BOOLTI-OS",
                "Android ${Build.VERSION.RELEASE} (SDK ${Build.VERSION.SDK_INT})"
            )
            .addHeader("X-BOOLTI-Locale", Locale.getDefault().toString())
            .addHeader("X-BOOLTI-Timezone", TimeZone.getDefault().id)
            .addHeader("X-BOOLTI-Screen", getScreenInfo())
            .addHeader("X-BOOLTI-Build-Type", getBuildType())
//            .addHeader("X-BOOLTI-Session-Id", sessionId) // 클라에서 안 보내면 서버에서 값 내려줌
            .addHeader("X-BOOLTI-Storage-Free", getStorageFree())
            .addHeader("X-BOOLTI-Memory", getMemoryInfo())
            .addHeader("X-BOOLTI-Battery", getBatteryInfo())
            .build()

        return chain.proceed(request)
    }

    private fun getAppVersion(): String {
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

    private fun getBuildType(): String {
        return if (BuildConfig.DEBUG) "debug" else "release"
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
                context.getSystemService(Context.ACTIVITY_SERVICE) as android.app.ActivityManager
            val memoryInfo = android.app.ActivityManager.MemoryInfo()
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
                android.content.IntentFilter(android.content.Intent.ACTION_BATTERY_CHANGED)
            )
            val level = batteryIntent?.getIntExtra(android.os.BatteryManager.EXTRA_LEVEL, -1) ?: -1
            val scale = batteryIntent?.getIntExtra(android.os.BatteryManager.EXTRA_SCALE, -1) ?: -1
            val status =
                batteryIntent?.getIntExtra(android.os.BatteryManager.EXTRA_STATUS, -1) ?: -1

            val batteryPercent = if (level >= 0 && scale > 0) {
                (level * 100 / scale)
            } else {
                -1
            }

            val statusText = when (status) {
                android.os.BatteryManager.BATTERY_STATUS_CHARGING -> "charging"
                android.os.BatteryManager.BATTERY_STATUS_DISCHARGING -> "discharging"
                android.os.BatteryManager.BATTERY_STATUS_FULL -> "full"
                android.os.BatteryManager.BATTERY_STATUS_NOT_CHARGING -> "not charging"
                else -> "unknown"
            }

            if (batteryPercent >= 0) "$statusText $batteryPercent%" else "unknown"
        } catch (e: Exception) {
            "unknown"
        }
    }
}
