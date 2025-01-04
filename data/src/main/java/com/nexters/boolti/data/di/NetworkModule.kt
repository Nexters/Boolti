package com.nexters.boolti.data.di

import com.jakewharton.retrofit2.converter.kotlinx.serialization.asConverterFactory
import com.nexters.boolti.data.BuildConfig
import com.nexters.boolti.data.datasource.AuthDataSource
import com.nexters.boolti.data.datasource.TokenDataSource
import com.nexters.boolti.data.network.AuthAuthenticator
import com.nexters.boolti.data.network.AuthInterceptor
import com.nexters.boolti.data.network.api.AuthFileService
import com.nexters.boolti.data.network.api.DeviceTokenService
import com.nexters.boolti.data.network.api.FileService
import com.nexters.boolti.data.network.api.GiftService
import com.nexters.boolti.data.network.api.HostService
import com.nexters.boolti.data.network.api.LoginService
import com.nexters.boolti.data.network.api.MemberService
import com.nexters.boolti.data.network.api.PopupService
import com.nexters.boolti.data.network.api.ReservationService
import com.nexters.boolti.data.network.api.ShowService
import com.nexters.boolti.data.network.api.SignUpService
import com.nexters.boolti.data.network.api.TicketService
import com.nexters.boolti.data.network.api.TicketingService
import com.nexters.boolti.data.network.api.UserService
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import kotlinx.serialization.json.Json
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.create
import java.util.concurrent.TimeUnit
import javax.inject.Named
import javax.inject.Singleton

@InstallIn(SingletonComponent::class)
@Module
internal object NetworkModule {
    @Singleton
    @Provides
    @Named("auth")
    fun provideAuthRetrofit(@Named("auth") okHttpClient: OkHttpClient): Retrofit {
        val json = Json {
            isLenient = true
            prettyPrint = true
            ignoreUnknownKeys = true
            coerceInputValues = true
        }
        return Retrofit.Builder()
            .baseUrl(BuildConfig.BASE_URL)
            .client(okHttpClient)
            .addConverterFactory(json.asConverterFactory("application/json".toMediaType()))
            .build()
    }

    @Singleton
    @Provides
    @Named("non-auth")
    fun provideNonAuthRetrofit(@Named("non-auth") okHttpClient: OkHttpClient): Retrofit {
        val json = Json {
            isLenient = true
            prettyPrint = true
            ignoreUnknownKeys = true
            coerceInputValues = true
        }
        return Retrofit.Builder()
            .baseUrl(BuildConfig.BASE_URL)
            .client(okHttpClient)
            .addConverterFactory(json.asConverterFactory("application/json".toMediaType()))
            .build()
    }

    @Singleton
    @Provides
    fun provideRetrofit(okHttpClient: OkHttpClient): Retrofit {
        val json = Json {
            isLenient = true
            prettyPrint = true
            ignoreUnknownKeys = true
            coerceInputValues = true
        }
        return Retrofit.Builder()
            .baseUrl(BuildConfig.BASE_URL)
            .client(okHttpClient)
            .addConverterFactory(json.asConverterFactory("application/json".toMediaType()))
            .build()
    }

    @Singleton
    @Provides
    fun provideApiService(retrofit: Retrofit): LoginService = retrofit.create()

    @Singleton
    @Provides
    @Named("auth")
    fun provideAuthApiService(@Named("auth") retrofit: Retrofit): LoginService = retrofit.create()

    @Singleton
    @Provides
    fun provideUserService(@Named("auth") retrofit: Retrofit): UserService = retrofit.create()

    @Singleton
    @Provides
    fun provideDeviceTokenService(@Named("auth") retrofit: Retrofit): DeviceTokenService =
        retrofit.create()

    @Singleton
    @Provides
    fun provideSignUpService(retrofit: Retrofit): SignUpService = retrofit.create()

    @Singleton
    @Provides
    fun provideShowService(retrofit: Retrofit): ShowService = retrofit.create()

    @Singleton
    @Provides
    fun provideTicketingService(@Named("auth") retrofit: Retrofit): TicketingService = retrofit.create()

    @Singleton
    @Provides
    fun provideGiftService(@Named("auth") retrofit: Retrofit): GiftService = retrofit.create()

    @Singleton
    @Provides
    fun provideTicketService(@Named("auth") retrofit: Retrofit): TicketService = retrofit.create()

    @Singleton
    @Provides
    fun provideReservationService(@Named("auth") retrofit: Retrofit): ReservationService = retrofit.create()

    @Singleton
    @Provides
    fun provideHostService(@Named("auth") retrofit: Retrofit): HostService = retrofit.create()

    @Singleton
    @Provides
    fun provideAuthFileService(@Named("auth") retrofit: Retrofit): AuthFileService = retrofit.create()

    @Singleton
    @Provides
    fun provideFileService(@Named("non-auth") retrofit: Retrofit): FileService = retrofit.create()

    @Singleton
    @Provides
    fun provideMemberService(@Named("non-auth") retrofit: Retrofit): MemberService = retrofit.create()

    @Singleton
    @Provides
    fun providePopupService(@Named("non-auth") retrofit: Retrofit): PopupService = retrofit.create()

    @Singleton
    @Provides
    @Named("auth")
    fun provideAuthOkHttpClient(interceptor: AuthInterceptor, authenticator: AuthAuthenticator): OkHttpClient {
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
    fun provideOkHttpClient(interceptor: AuthInterceptor): OkHttpClient {
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
            .addInterceptor(interceptor)
            .addInterceptor(loggingInterceptor)
            .build()
    }

    @Singleton
    @Provides
    @Named("non-auth")
    fun provideNoneAuthOkHttpClient(): OkHttpClient {
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
    ): AuthAuthenticator = AuthAuthenticator(tokenDataSource, authDataSource)
}
