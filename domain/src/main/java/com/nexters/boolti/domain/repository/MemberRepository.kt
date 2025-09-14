package com.nexters.boolti.domain.repository

import com.nexters.boolti.domain.model.Link
import com.nexters.boolti.domain.model.Show
import com.nexters.boolti.domain.model.User

interface MemberRepository {
    suspend fun getMember(userCode: String): Result<User.Others>
    suspend fun getLinks(userCode: String, refresh: Boolean = false): Result<List<Link>>
    suspend fun getPerformedShows(userCode: String): Result<List<Show>>
    suspend fun getVideoLinks(userCode: String, refresh: Boolean = false): Result<List<String>>
}
