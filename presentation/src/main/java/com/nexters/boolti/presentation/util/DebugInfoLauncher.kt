package com.nexters.boolti.presentation.util

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.os.Handler
import android.os.Looper
import android.view.PixelCopy
import com.nexters.boolti.presentation.BuildConfig
import timber.log.Timber
import java.io.File
import kotlin.concurrent.thread
import androidx.core.graphics.createBitmap

const val EXTRA_CURRENT_SCREEN = "currentScreen"
const val EXTRA_SCREENSHOT_PATH = "screenshotPath"

/**
 * 디버그 정보 팝업(debug 소스셋의 DebugInfoActivity)을 띄운다. debug 빌드에서만 부른다
 */
fun openDebugInfo(context: Context, currentScreen: String?, screenshotPath: String? = null) {
    context.startActivity(
        Intent()
            .setClassName(context, "${BuildConfig.LIBRARY_PACKAGE_NAME}.screen.debug.info.DebugInfoActivity")
            .putExtra(EXTRA_CURRENT_SCREEN, currentScreen)
            .putExtra(EXTRA_SCREENSHOT_PATH, screenshotPath)
    )
}

/**
 * 팝업이 덮기 전에 현재 화면을 캡처하고 팝업을 띄운다. 캡처에 실패해도 팝업은 띄운다
 *
 * 다이얼로그·바텀시트처럼 별도 창으로 뜬 화면은 캡처되지 않는다
 */
fun openDebugInfoWithScreenshot(activity: Activity, currentScreen: String?) {
    val decorView = activity.window.decorView
    if (decorView.width == 0 || decorView.height == 0) {
        openDebugInfo(activity, currentScreen)
        return
    }
    val bitmap = createBitmap(decorView.width, decorView.height)
    PixelCopy.request(
        activity.window,
        bitmap,
        { result ->
            if (result != PixelCopy.SUCCESS) {
                Timber.w("화면 캡처 실패: $result")
                openDebugInfo(activity, currentScreen)
                return@request
            }
            // 이미지 압축은 메인 스레드를 멈추지 않게 따로 돌린다
            thread {
                val file = File(activity.cacheDir, "debug_screenshot.jpg")
                file.outputStream().use { bitmap.compress(Bitmap.CompressFormat.JPEG, 80, it) }
                bitmap.recycle()
                activity.runOnUiThread { openDebugInfo(activity, currentScreen, file.path) }
            }
        },
        Handler(Looper.getMainLooper()),
    )
}
