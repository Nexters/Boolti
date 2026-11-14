package com.nexters.boolti.data.repository

import com.nexters.boolti.data.cache.CacheKeys
import com.nexters.boolti.data.cache.CacheStore
import com.nexters.boolti.data.datasource.RemoteMemberDataSource
import com.nexters.boolti.domain.model.Link
import com.nexters.boolti.domain.model.Show
import com.nexters.boolti.domain.model.User
import com.nexters.boolti.domain.repository.MemberRepository
import com.nexters.boolti.domain.util.suspendRunCatching
import javax.inject.Inject

internal class MemberRepositoryImpl @Inject constructor(
    private val remoteMemberDataSource: RemoteMemberDataSource,
    private val cacheStore: CacheStore,
) : MemberRepository {
    override suspend fun getMember(userCode: String): Result<User.Others> = runCatching {
        remoteMemberDataSource.getMember(userCode).toDomain()
    }

    override suspend fun getLinks(userCode: String): Result<List<Link>> = suspendRunCatching {
        remoteMemberDataSource.getLinks(userCode).map { it.toDomain() }
    }

    override suspend fun getPerformedShows(userCode: String): Result<List<Show>> = suspendRunCatching {
        cacheStore.getOrFetch(CacheKeys.performedShows(userCode)) {
            remoteMemberDataSource.getPerformedShows(userCode).map { it.toDomain() }
        }
    }

    override suspend fun getVideoLinks(userCode: String): Result<List<String>> = suspendRunCatching {
        remoteMemberDataSource.getVideoLinks(userCode)
    }
}
