package com.nexters.boolti.data.di

import com.google.firebase.remoteconfig.FirebaseRemoteConfig
import com.nexters.boolti.data.datasource.RemoteConfigDataSource
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@InstallIn(SingletonComponent::class)
@Module
object DataSourceModule {
    @Singleton
    @Provides
    fun provideRemoteConfigDataSource(remoteConfig: FirebaseRemoteConfig): RemoteConfigDataSource {
        return RemoteConfigDataSource(remoteConfig)
    }
}
