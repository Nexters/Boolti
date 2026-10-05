package com.nexters.boolti.presentation.util

import android.app.Activity
import android.app.Application
import android.os.Build
import android.os.Bundle
import androidx.annotation.RequiresApi
import com.nexters.boolti.presentation.screen.MainActivity

/**
 * 맨 앞 Activity에서 스크린샷을 찍으면 디버그 정보 팝업을 띄운다. debug 빌드에서만 등록한다
 *
 * 맨 앞(resumed) Activity에만 콜백을 걸어서, 팝업이 떠 있을 때 찍어도 팝업이 겹치지 않는다
 */
@RequiresApi(Build.VERSION_CODES.UPSIDE_DOWN_CAKE)
class ScreenCaptureWatcher : Application.ActivityLifecycleCallbacks {
    private var registered: Pair<Activity, Activity.ScreenCaptureCallback>? = null

    override fun onActivityResumed(activity: Activity) {
        if (activity.javaClass.simpleName == "DebugInfoActivity") return

        val callback = Activity.ScreenCaptureCallback {
            val currentScreen = if (activity is MainActivity) {
                DebugManager.currentScreen
            } else {
                activity.javaClass.simpleName
            }
            openDebugInfoWithScreenshot(activity, currentScreen)
        }
        activity.registerScreenCaptureCallback(activity.mainExecutor, callback)
        registered = activity to callback
    }

    override fun onActivityPaused(activity: Activity) {
        val (registeredActivity, callback) = registered ?: return
        if (registeredActivity != activity) return
        activity.unregisterScreenCaptureCallback(callback)
        registered = null
    }

    override fun onActivityCreated(activity: Activity, savedInstanceState: Bundle?) = Unit
    override fun onActivityStarted(activity: Activity) = Unit
    override fun onActivityStopped(activity: Activity) = Unit
    override fun onActivitySaveInstanceState(activity: Activity, outState: Bundle) = Unit
    override fun onActivityDestroyed(activity: Activity) = Unit
}
