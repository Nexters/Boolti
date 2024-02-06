package com.nexters.boolti.data.di

import com.nexters.boolti.data.datasource.TicketingRepositoryImpl
import com.nexters.boolti.data.repository.AuthRepositoryImpl
import com.nexters.boolti.data.repository.ConfigRepositoryImpl
import com.nexters.boolti.data.repository.ShowRepositoryImpl
import com.nexters.boolti.domain.repository.AuthRepository
import com.nexters.boolti.domain.repository.ConfigRepository
import com.nexters.boolti.domain.repository.ShowRepository
import com.nexters.boolti.domain.repository.TicketingRepository
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent

@InstallIn(SingletonComponent::class)
@Module
abstract class RepositoryModule {
    @Binds
    abstract fun bindConfigRepository(repository: ConfigRepositoryImpl): ConfigRepository

    @Binds
    abstract fun bindAuthRepository(repository: AuthRepositoryImpl): AuthRepository

    @Binds
    abstract fun bindShowRepository(repositoryImpl: ShowRepositoryImpl): ShowRepository

    @Binds
    abstract fun bindTicketingRepository(repository: TicketingRepositoryImpl): TicketingRepository
}
