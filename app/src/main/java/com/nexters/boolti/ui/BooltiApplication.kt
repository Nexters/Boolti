package com.nexters.boolti.ui

import android.app.Application
import com.kakao.sdk.common.KakaoSdk
import dagger.hilt.android.HiltAndroidApp

@HiltAndroidApp
class BooltiApplication : Application() {

    override fun onCreate() {
        super.onCreate()

        // todo : 네이티브 앱 키 넣어야 함
        KakaoSdk.init(this, "YOUR_KEY")
    }
}
