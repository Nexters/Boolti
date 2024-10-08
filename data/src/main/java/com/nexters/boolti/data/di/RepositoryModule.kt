package com.nexters.boolti.data.di

import com.nexters.boolti.data.repository.AuthRepositoryImpl
import com.nexters.boolti.data.repository.ConfigRepositoryImpl
import com.nexters.boolti.data.repository.FileRepositoryImpl
import com.nexters.boolti.data.repository.GiftRepositoryImpl
import com.nexters.boolti.data.repository.HostRepositoryImpl
import com.nexters.boolti.data.repository.MemberRepositoryImpl
import com.nexters.boolti.data.repository.ReservationRepositoryImpl
import com.nexters.boolti.data.repository.ShowRepositoryImpl
import com.nexters.boolti.data.repository.TicketRepositoryImpl
import com.nexters.boolti.data.repository.TicketingRepositoryImpl
import com.nexters.boolti.domain.repository.AuthRepository
import com.nexters.boolti.domain.repository.ConfigRepository
import com.nexters.boolti.domain.repository.FileRepository
import com.nexters.boolti.domain.repository.GiftRepository
import com.nexters.boolti.domain.repository.HostRepository
import com.nexters.boolti.domain.repository.MemberRepository
import com.nexters.boolti.domain.repository.ReservationRepository
import com.nexters.boolti.domain.repository.ShowRepository
import com.nexters.boolti.domain.repository.TicketRepository
import com.nexters.boolti.domain.repository.TicketingRepository
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent

@InstallIn(SingletonComponent::class)
@Module
internal abstract class RepositoryModule {
    @Binds
    abstract fun bindConfigRepository(repository: ConfigRepositoryImpl): ConfigRepository

    @Binds
    abstract fun bindAuthRepository(repository: AuthRepositoryImpl): AuthRepository

    @Binds
    abstract fun bindShowRepository(repositoryImpl: ShowRepositoryImpl): ShowRepository

    @Binds
    abstract fun bindTicketingRepository(repository: TicketingRepositoryImpl): TicketingRepository

    @Binds
    abstract fun bindGiftRepository(repository: GiftRepositoryImpl): GiftRepository

    @Binds
    abstract fun bindTicketRepository(repository: TicketRepositoryImpl): TicketRepository

    @Binds
    abstract fun bindReservationRepository(repository: ReservationRepositoryImpl): ReservationRepository

    @Binds
    abstract fun bindHostRepository(repository: HostRepositoryImpl): HostRepository

    @Binds
    abstract fun bindFileRepository(repository: FileRepositoryImpl): FileRepository

    @Binds
    abstract fun bindMemberRepository(repository: MemberRepositoryImpl): MemberRepository
}
