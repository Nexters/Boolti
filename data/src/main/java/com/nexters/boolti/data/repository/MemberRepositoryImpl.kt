package com.nexters.boolti.data.repository

import com.nexters.boolti.data.datasource.LocalMemberDataSource
import com.nexters.boolti.data.datasource.RemoteMemberDataSource
import com.nexters.boolti.domain.model.Link
import com.nexters.boolti.domain.model.Show
import com.nexters.boolti.domain.model.User
import com.nexters.boolti.domain.repository.MemberRepository
import javax.inject.Inject

internal class MemberRepositoryImpl @Inject constructor(
    private val remoteMemberDataSource: RemoteMemberDataSource,
    private val localMemberDataSource: LocalMemberDataSource,
) : MemberRepository {
    override suspend fun getMember(userCode: String): Result<User.Others> = runCatching {
        remoteMemberDataSource.getMember(userCode).toDomain()
    }

    override suspend fun getLinks(userCode: String, refresh: Boolean): Result<List<Link>> =
        runCatching {
            suspend fun getRemoteLinksAndCache(): List<Link> {
                return remoteMemberDataSource.getLinks(userCode).also {
                    localMemberDataSource.setLink(userCode, it)
                }.map { it.toDomain() }
            }

            if (refresh) {
                getRemoteLinksAndCache()
            } else {
                localMemberDataSource.getLinks(userCode)?.map { it.toDomain() }
                    ?: getRemoteLinksAndCache()
            }
        }

    override suspend fun getPerformedShows(userCode: String): Result<List<Show>> = runCatching {
        localMemberDataSource.getPerformedShows(userCode)?.map { it.toDomain() }
            ?: remoteMemberDataSource.getPerformedShows(userCode).also {
                localMemberDataSource.setPerformedShows(userCode, it)
            }.map { it.toDomain() }
    }
}
