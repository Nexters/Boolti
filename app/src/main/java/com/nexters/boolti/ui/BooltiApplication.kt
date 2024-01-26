package com.nexters.boolti.ui

import android.app.Application
import com.kakao.sdk.common.KakaoSdk
import com.nexters.boolti.BuildConfig
import dagger.hilt.android.HiltAndroidApp
import timber.log.Timber

@HiltAndroidApp
class BooltiApplication : Application() {

    override fun onCreate() {
        super.onCreate()

        if (BuildConfig.DEBUG) Timber.plant(Timber.DebugTree())
        KakaoSdk.init(this, BuildConfig.KAKAO_APP_KEY)
    }
}
