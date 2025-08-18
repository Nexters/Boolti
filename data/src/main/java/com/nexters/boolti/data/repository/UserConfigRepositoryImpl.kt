package com.nexters.boolti.data.repository

import com.nexters.boolti.data.datasource.AuthDataSource
import com.nexters.boolti.data.datasource.UserDataSource
import com.nexters.boolti.data.network.response.toDomain
import com.nexters.boolti.domain.model.Duplicated
import com.nexters.boolti.domain.model.Sns
import com.nexters.boolti.domain.model.ToggleResult
import com.nexters.boolti.domain.model.UserCode
import com.nexters.boolti.domain.repository.UserConfigRepository
import com.nexters.boolti.domain.request.toDto
import kotlinx.coroutines.flow.firstOrNull
import javax.inject.Inject

internal class UserConfigRepositoryImpl @Inject constructor(
    private val userDataSource: UserDataSource,
    private val authDataSource: AuthDataSource,
) : UserConfigRepository {
    override suspend fun setUpcomingShowVisible(visible: Boolean): Result<ToggleResult> =
        runCatching {
            userDataSource.setUpcomingShowVisible(visible).toDomain()
        }

    override suspend fun setPastShowVisible(visible: Boolean): Result<ToggleResult> =
        runCatching {
            userDataSource.setPastShowVisible(visible).toDomain()
        }

    override suspend fun checkUserCodeDuplicated(userCode: UserCode): Result<Duplicated> =
        runCatching {
            userDataSource.checkUserCodeDuplicated(userCode).isDuplicated
        }

    override suspend fun saveNickname(nickname: String): Result<String> =
        runCatching {
            userDataSource.saveNickname(nickname).nickname
        }.onSuccess { nickname ->
            val user = authDataSource.user.firstOrNull()
            if (user != null) {
                authDataSource.updateUser(user.copy(nickname = nickname))
            }
        }

    override suspend fun saveUserCode(userCode: UserCode): Result<String> =
        runCatching {
            userDataSource.saveNickname(userCode).nickname
        }.onSuccess { nickname ->
            val user = authDataSource.user.firstOrNull()
            if (user != null) {
                authDataSource.updateUser(user.copy(nickname = nickname))
            }
        }

    override suspend fun saveIntroduce(introduce: String): Result<String> =
        runCatching {
            userDataSource.saveIntroduce(introduce).introduction
        }.onSuccess { introduce ->
            val user = authDataSource.user.firstOrNull()
            if (user != null) {
                authDataSource.updateUser(user.copy(introduction = introduce))
            }
        }

    override suspend fun saveSns(snsList: List<Sns>): Result<Unit> =
        runCatching {
            userDataSource.saveSns(snsList)
        }.onSuccess {
            val user = authDataSource.user.firstOrNull()
            if (user != null) {
                authDataSource.updateUser(user.copy(sns = snsList.map { it.toDto() }))
            }
        }
}
