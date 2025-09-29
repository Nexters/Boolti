package com.nexters.boolti.ui

import android.app.Application
import com.kakao.sdk.common.KakaoSdk
import com.nexters.boolti.BuildConfig
import com.nexters.boolti.common.tracker.AppTracker
import dagger.hilt.android.HiltAndroidApp
import timber.log.Timber

@HiltAndroidApp
class BooltiApplication : Application() {

    override fun onCreate() {
        super.onCreate()

        initLogger()
        initKakaoSdk()
        initTracker()
    }

    private fun initLogger() {
        if (BuildConfig.DEBUG) Timber.plant(Timber.DebugTree())
    }

    private fun initKakaoSdk() {
        KakaoSdk.init(this, BuildConfig.KAKAO_APP_KEY)
    }

    private fun initTracker() {
        AppTracker.initialize(this)
    }
}
