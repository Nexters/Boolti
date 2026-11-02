package com.nexters.boolti.data.repository

import android.app.ActivityManager
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.os.BatteryManager
import android.os.Build
import com.nexters.boolti.data.BuildConfig
import com.nexters.boolti.domain.model.DeviceInfo
import com.nexters.boolti.domain.repository.DeviceInfoRepository
import dagger.hilt.android.qualifiers.ApplicationContext
import java.util.Locale
import java.util.TimeZone
import javax.inject.Inject

internal class DeviceInfoRepositoryImpl @Inject constructor(
    @ApplicationContext private val context: Context,
) : DeviceInfoRepository {

    // 앱 실행 중에 바뀌지 않는 값은 한 번만 계산한다
    private val appVersion: String? by lazy { readAppVersion() }
    private val os = "Android ${Build.VERSION.RELEASE} (SDK ${Build.VERSION.SDK_INT})"
    private val buildType = if (BuildConfig.DEBUG) "debug" else "release"

    override fun getDeviceInfo(): DeviceInfo = DeviceInfo(
        appVersion = appVersion,
        model = Build.MODEL,
        os = os,
        locale = Locale.getDefault().toString(),
        timezone = TimeZone.getDefault().id,
        buildType = buildType,
        screen = getScreenInfo(),
        storageFree = getStorageFree(),
        memory = getMemoryInfo(),
        battery = getBatteryInfo(),
    )

    private fun readAppVersion(): String? {
        return try {
            val packageInfo = context.packageManager.getPackageInfo(context.packageName, 0)
            packageInfo.versionName
        } catch (e: Exception) {
            null
        }
    }

    private fun getScreenInfo(): String? {
        return try {
            val displayMetrics = context.resources.displayMetrics
            "${displayMetrics.widthPixels}x${displayMetrics.heightPixels} / ${displayMetrics.densityDpi}dpi"
        } catch (e: Exception) {
            null
        }
    }

    private fun getStorageFree(): String? {
        return try {
            formatStorageFree(context.filesDir.freeSpace)
        } catch (e: Exception) {
            null
        }
    }

    private fun getMemoryInfo(): String? {
        return try {
            val activityManager =
                context.getSystemService(Context.ACTIVITY_SERVICE) as ActivityManager
            val memoryInfo = ActivityManager.MemoryInfo()
            activityManager.getMemoryInfo(memoryInfo)

            val availMB = memoryInfo.availMem / (1024 * 1024)
            val totalMB = memoryInfo.totalMem / (1024 * 1024)
            "avail=${availMB}MB / total=${totalMB}MB"
        } catch (e: Exception) {
            null
        }
    }

    private fun getBatteryInfo(): String? {
        return try {
            val batteryIntent = context.registerReceiver(
                null,
                IntentFilter(Intent.ACTION_BATTERY_CHANGED)
            ) ?: return null
            formatBattery(
                level = batteryIntent.getIntExtra(BatteryManager.EXTRA_LEVEL, -1),
                scale = batteryIntent.getIntExtra(BatteryManager.EXTRA_SCALE, -1),
                status = batteryIntent.getIntExtra(BatteryManager.EXTRA_STATUS, -1),
            )
        } catch (e: Exception) {
            null
        }
    }
}

// 헤더 값이라 기기 언어와 상관없이 소수점을 '.'으로 쓴다
internal fun formatStorageFree(freeBytes: Long): String =
    String.format(Locale.US, "%.1fGB", freeBytes / (1024.0 * 1024 * 1024))

internal fun formatBattery(level: Int, scale: Int, status: Int): String? {
    if (level < 0 || scale <= 0) return null

    val statusText = when (status) {
        BatteryManager.BATTERY_STATUS_CHARGING -> "charging"
        BatteryManager.BATTERY_STATUS_DISCHARGING -> "discharging"
        BatteryManager.BATTERY_STATUS_FULL -> "full"
        BatteryManager.BATTERY_STATUS_NOT_CHARGING -> "not charging"
        else -> "unknown"
    }
    return "$statusText ${level * 100 / scale}%"
}
