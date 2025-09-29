package com.nexters.boolti.data.di

import com.jakewharton.retrofit2.converter.kotlinx.serialization.asConverterFactory
import com.nexters.boolti.data.BuildConfig
import com.nexters.boolti.data.datasource.AuthTokenDataSource
import com.nexters.boolti.data.datasource.TokenDataSource
import com.nexters.boolti.data.di.qualifier.AuthOkHttpClient
import com.nexters.boolti.data.di.qualifier.AuthRetrofit
import com.nexters.boolti.data.di.qualifier.NonAuthOkHttpClient
import com.nexters.boolti.data.di.qualifier.NonAuthRetrofit
import com.nexters.boolti.data.di.qualifier.YouTubeOkHttpClient
import com.nexters.boolti.data.di.qualifier.YouTubeRetrofit
import com.nexters.boolti.data.network.AuthAuthenticator
import com.nexters.boolti.data.network.AuthInterceptor
import com.nexters.boolti.data.network.CustomHeaderInterceptor
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
import com.nexters.boolti.data.network.api.YouTubeService
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
import javax.inject.Singleton

@InstallIn(SingletonComponent::class)
@Module
internal object NetworkModule {
    @Singleton
    @Provides
    @AuthRetrofit
    fun provideAuthRetrofit(@AuthOkHttpClient okHttpClient: OkHttpClient): Retrofit {
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
    @NonAuthRetrofit
    fun provideNonAuthRetrofit(@NonAuthOkHttpClient okHttpClient: OkHttpClient): Retrofit {
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
    @AuthRetrofit
    fun provideAuthApiService(@AuthRetrofit retrofit: Retrofit): LoginService = retrofit.create()

    @Singleton
    @Provides
    fun provideUserService(@AuthRetrofit retrofit: Retrofit): UserService = retrofit.create()

    @Singleton
    @Provides
    fun provideDeviceTokenService(@AuthRetrofit retrofit: Retrofit): DeviceTokenService =
        retrofit.create()

    @Singleton
    @Provides
    fun provideSignUpService(retrofit: Retrofit): SignUpService = retrofit.create()

    @Singleton
    @Provides
    fun provideShowService(retrofit: Retrofit): ShowService = retrofit.create()

    @Singleton
    @Provides
    fun provideTicketingService(@AuthRetrofit retrofit: Retrofit): TicketingService =
        retrofit.create()

    @Singleton
    @Provides
    fun provideGiftService(@AuthRetrofit retrofit: Retrofit): GiftService = retrofit.create()

    @Singleton
    @Provides
    fun provideTicketService(@AuthRetrofit retrofit: Retrofit): TicketService = retrofit.create()

    @Singleton
    @Provides
    fun provideReservationService(@AuthRetrofit retrofit: Retrofit): ReservationService =
        retrofit.create()

    @Singleton
    @Provides
    fun provideHostService(@AuthRetrofit retrofit: Retrofit): HostService = retrofit.create()

    @Singleton
    @Provides
    fun provideAuthFileService(@AuthRetrofit retrofit: Retrofit): AuthFileService =
        retrofit.create()

    @Singleton
    @Provides
    fun provideFileService(@NonAuthRetrofit retrofit: Retrofit): FileService = retrofit.create()

    @Singleton
    @Provides
    fun provideMemberService(@NonAuthRetrofit retrofit: Retrofit): MemberService =
        retrofit.create()

    @Singleton
    @Provides
    fun providePopupService(@NonAuthRetrofit retrofit: Retrofit): PopupService = retrofit.create()

    @Singleton
    @Provides
    @YouTubeRetrofit
    fun provideYouTubeRetrofit(@YouTubeOkHttpClient okHttpClient: OkHttpClient): Retrofit {
        val json = Json {
            isLenient = true
            prettyPrint = true
            ignoreUnknownKeys = true
            coerceInputValues = true
        }
        return Retrofit.Builder()
            .baseUrl("https://www.googleapis.com/youtube/v3/")
            .client(okHttpClient)
            .addConverterFactory(json.asConverterFactory("application/json".toMediaType()))
            .build()
    }

    @Singleton
    @Provides
    @YouTubeOkHttpClient
    fun provideYouTubeOkHttpClient(customHeaderInterceptor: CustomHeaderInterceptor): OkHttpClient {
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
            .addInterceptor(customHeaderInterceptor)
            .addInterceptor(loggingInterceptor)
            .build()
    }

    @Singleton
    @Provides
    fun provideYouTubeService(@YouTubeRetrofit retrofit: Retrofit): YouTubeService =
        retrofit.create()

    @Singleton
    @Provides
    @AuthOkHttpClient
    fun provideAuthOkHttpClient(
        authInterceptor: AuthInterceptor,
        customHeaderInterceptor: CustomHeaderInterceptor,
        authenticator: AuthAuthenticator
    ): OkHttpClient {
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
            .addInterceptor(customHeaderInterceptor)
            .addInterceptor(authInterceptor)
            .addInterceptor(loggingInterceptor)
            .build()
    }

    @Singleton
    @Provides
    fun provideOkHttpClient(
        authInterceptor: AuthInterceptor,
        customHeaderInterceptor: CustomHeaderInterceptor,
    ): OkHttpClient {
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
            .addInterceptor(customHeaderInterceptor)
            .addInterceptor(authInterceptor)
            .addInterceptor(loggingInterceptor)
            .build()
    }

    @Singleton
    @Provides
    @NonAuthOkHttpClient
    fun provideNoneAuthOkHttpClient(customHeaderInterceptor: CustomHeaderInterceptor): OkHttpClient {
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
            .addInterceptor(customHeaderInterceptor)
            .addInterceptor(loggingInterceptor)
            .build()
    }

    @Singleton
    @Provides
    fun provideAuthInterceptor(tokenDataSource: TokenDataSource): AuthInterceptor =
        AuthInterceptor(tokenDataSource)

    @Singleton
    @Provides
    fun provideAuthenticator(
        authTokenDataSource: AuthTokenDataSource
    ): AuthAuthenticator = AuthAuthenticator(authTokenDataSource)
}
