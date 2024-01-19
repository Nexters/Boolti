package com.nexters.boolti.ui

import android.app.Application
import com.kakao.sdk.common.KakaoSdk
import com.nexters.boolti.BuildConfig
import dagger.hilt.android.HiltAndroidApp

@HiltAndroidApp
class BooltiApplication : Application() {

    override fun onCreate() {
        super.onCreate()

        KakaoSdk.init(this, BuildConfig.KAKAO_APP_KEY)
    }
}
