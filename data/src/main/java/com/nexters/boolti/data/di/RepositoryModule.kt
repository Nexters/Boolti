package com.nexters.boolti.data.di

import com.nexters.boolti.data.repository.ConfigRepositoryImpl
import com.nexters.boolti.domain.repository.ConfigRepository
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent

@InstallIn(SingletonComponent::class)
@Module
abstract class RepositoryModule {
    @Binds
    abstract fun bindConfigRepository(repository: ConfigRepositoryImpl): ConfigRepository
}
