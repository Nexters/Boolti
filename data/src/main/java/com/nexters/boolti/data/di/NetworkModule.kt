package com.nexters.boolti.data.di

import com.nexters.boolti.data.BuildConfig
import com.nexters.boolti.data.datasource.TokenDataSource
import com.nexters.boolti.data.network.ApiService
import com.nexters.boolti.data.network.AuthAuthenticator
import com.nexters.boolti.data.network.AuthDataSource
import com.nexters.boolti.data.network.AuthInterceptor
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import java.util.concurrent.TimeUnit
import javax.inject.Singleton

@InstallIn(SingletonComponent::class)
@Module
object NetworkModule {
    @Singleton
    @Provides
    fun provideRetrofit(okHttpClient: OkHttpClient): Retrofit = Retrofit.Builder()
        .baseUrl(BuildConfig.BASE_URL)
        .client(okHttpClient)
        // TODO Converter
        .build()

    @Singleton
    @Provides
    fun provideApiService(retrofit: Retrofit): ApiService = retrofit.create(ApiService::class.java)

    @Singleton
    @Provides
    fun provideOkHttpClient(interceptor: AuthInterceptor, authenticator: AuthAuthenticator): OkHttpClient {
        val loggingInterceptor = HttpLoggingInterceptor().apply {
            level = if (BuildConfig.DEBUG) {
                HttpLoggingInterceptor.Level.BODY
            } else {
                HttpLoggingInterceptor.Level.NONE
            }
        }
        return OkHttpClient.Builder()
            .connectTimeout(10, TimeUnit.SECONDS)
            .readTimeout(10, TimeUnit.SECONDS)
            .authenticator(authenticator)
            .addInterceptor(interceptor)
            .addInterceptor(loggingInterceptor)
            .build()
    }

    @Singleton
    @Provides
    fun provideAuthInterceptor(tokenDataSource: TokenDataSource): AuthInterceptor = AuthInterceptor(tokenDataSource)

    @Singleton
    @Provides
    fun provideAuthenticator(
        tokenDataSource: TokenDataSource,
        authDataSource: AuthDataSource,
        apiService: ApiService,
    ): AuthAuthenticator = AuthAuthenticator(tokenDataSource, authDataSource, apiService)
}
