package com.nexters.boolti.data.di

import com.nexters.boolti.data.BuildConfig
import com.nexters.boolti.data.repository.DebugReportRepositoryImpl
import com.nexters.boolti.domain.qualifier.IoDispatcher
import com.nexters.boolti.domain.repository.DebugReportRepository
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import kotlinx.coroutines.CoroutineDispatcher
import okhttp3.OkHttpClient
import java.util.concurrent.TimeUnit
import javax.inject.Singleton

@InstallIn(SingletonComponent::class)
@Module
internal object DebugReportModule {
    @Singleton
    @Provides
    fun provideDebugReportRepository(
        @IoDispatcher ioDispatcher: CoroutineDispatcher,
    ): DebugReportRepository = DebugReportRepositoryImpl(
        webhookUrl = BuildConfig.DISCORD_DEBUG_INFO_WEBHOOK_URL,
        // 앱 API용 클라이언트는 인증 토큰 헤더가 붙어서 따로 만든다
        client = OkHttpClient.Builder()
            .writeTimeout(30, TimeUnit.SECONDS)
            .build(),
        ioDispatcher = ioDispatcher,
    )
}
