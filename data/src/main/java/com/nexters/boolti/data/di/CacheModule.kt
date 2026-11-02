package com.nexters.boolti.data.di

import com.nexters.boolti.data.cache.CacheStore
import com.nexters.boolti.data.cache.MemoryCacheStore
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent

@InstallIn(SingletonComponent::class)
@Module
internal abstract class CacheModule {
    @Binds
    abstract fun bindCacheStore(store: MemoryCacheStore): CacheStore
}
