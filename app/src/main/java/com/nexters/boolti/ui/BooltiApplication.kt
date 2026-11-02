package com.nexters.boolti.ui

import android.app.Application
import android.os.Build
import com.kakao.sdk.common.KakaoSdk
import com.mangbaam.logger.CollectableDebugTree
import com.nexters.boolti.BuildConfig
import com.nexters.boolti.common.tracker.AppTracker
import com.nexters.boolti.logger.CrashlyticsTree
import com.nexters.boolti.presentation.util.ScreenCaptureWatcher
import dagger.hilt.android.HiltAndroidApp
import timber.log.Timber

@HiltAndroidApp
class BooltiApplication : Application() {

    override fun onCreate() {
        super.onCreate()

        initLogger()
        initKakaoSdk()
        initTracker()
        initScreenCaptureWatcher()
    }

    private fun initLogger() {
        Timber.plant(CrashlyticsTree())
        if (BuildConfig.DEBUG) Timber.plant(CollectableDebugTree())
    }

    private fun initKakaoSdk() {
        KakaoSdk.init(this, BuildConfig.KAKAO_APP_KEY)
    }

    private fun initTracker() {
        AppTracker.initialize(this)
    }

    private fun initScreenCaptureWatcher() {
        if (BuildConfig.DEBUG && Build.VERSION.SDK_INT >= Build.VERSION_CODES.UPSIDE_DOWN_CAKE) {
            registerActivityLifecycleCallbacks(ScreenCaptureWatcher())
        }
    }
}
