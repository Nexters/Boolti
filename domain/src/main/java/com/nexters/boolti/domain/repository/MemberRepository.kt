package com.nexters.boolti.domain.repository

import com.nexters.boolti.domain.model.User

interface MemberRepository {
    suspend fun getMember(userCode: String): User.Others
}
