package com.nexters.boolti.data.network

import com.nexters.boolti.domain.repository.DeviceInfoRepository
import okhttp3.Interceptor
import okhttp3.Response
import javax.inject.Inject

internal class CustomHeaderInterceptor @Inject constructor(
    private val deviceInfoRepository: DeviceInfoRepository,
) : Interceptor {

    override fun intercept(chain: Interceptor.Chain): Response {
        val info = deviceInfoRepository.getDeviceInfo()
        val request = chain.request().newBuilder()
            .addHeader("X-BOOLTI-App-Version", info.appVersion ?: UNKNOWN)
            .addHeader("X-BOOLTI-Device-Model", info.model)
            .addHeader("X-BOOLTI-OS", info.os)
            .addHeader("X-BOOLTI-Locale", info.locale)
            .addHeader("X-BOOLTI-Timezone", info.timezone)
            .addHeader("X-BOOLTI-Screen", info.screen ?: UNKNOWN)
            .addHeader("X-BOOLTI-Build-Type", info.buildType)
            .addHeader("X-BOOLTI-Storage-Free", info.storageFree ?: UNKNOWN)
            .addHeader("X-BOOLTI-Memory", info.memory ?: UNKNOWN)
            .addHeader("X-BOOLTI-Battery", info.battery ?: UNKNOWN)
            .build()

        return chain.proceed(request)
    }

    private companion object {
        const val UNKNOWN = "unknown"
    }
}
