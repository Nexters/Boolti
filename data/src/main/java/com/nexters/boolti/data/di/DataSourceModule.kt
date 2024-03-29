package com.nexters.boolti.data.di

import android.content.Context
import com.google.firebase.remoteconfig.FirebaseRemoteConfig
import com.nexters.boolti.data.datasource.AuthDataSource
import com.nexters.boolti.data.datasource.PolicyDataSource
import com.nexters.boolti.data.datasource.RemoteConfigDataSource
import com.nexters.boolti.data.datasource.TokenDataSource
import com.nexters.boolti.data.network.api.LoginService
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@InstallIn(SingletonComponent::class)
@Module
internal object DataSourceModule {
    @Singleton
    @Provides
    fun provideRemoteConfigDataSource(remoteConfig: FirebaseRemoteConfig): RemoteConfigDataSource =
        RemoteConfigDataSource(remoteConfig)

    @Singleton
    @Provides
    fun provideAuthDataSource(
        @ApplicationContext context: Context,
        loginService: LoginService,
    ): AuthDataSource = AuthDataSource(context, loginService)

    @Singleton
    @Provides
    fun provideTokenDataSource(@ApplicationContext context: Context): TokenDataSource = TokenDataSource(context)

    @Singleton
    @Provides
    fun providePolicyDataSource(@ApplicationContext context: Context): PolicyDataSource = PolicyDataSource(context)
}
