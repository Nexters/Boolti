package com.nexters.boolti.data.repository

import com.nexters.boolti.data.datasource.MemberDataSource
import com.nexters.boolti.domain.model.User
import com.nexters.boolti.domain.repository.MemberRepository
import javax.inject.Inject

internal class MemberRepositoryImpl @Inject constructor(
    private val memberDataSource: MemberDataSource,
) : MemberRepository {
    override suspend fun getMember(userCode: String): User.Others {
        return memberDataSource.getMember(userCode).toDomain()
    }
}
