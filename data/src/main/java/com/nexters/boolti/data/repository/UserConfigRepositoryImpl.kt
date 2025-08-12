package com.nexters.boolti.data.repository

import com.nexters.boolti.data.datasource.UserDataSource
import com.nexters.boolti.data.network.response.toDomain
import com.nexters.boolti.domain.model.ToggleResult
import com.nexters.boolti.domain.repository.UserConfigRepository
import javax.inject.Inject

internal class UserConfigRepositoryImpl @Inject constructor(
    private val userDataSource: UserDataSource,
) : UserConfigRepository {
    override suspend fun setUpcomingShowVisible(visible: Boolean): Result<ToggleResult> =
        runCatching {
            userDataSource.setUpcomingShowVisible(visible).toDomain()
        }

    override suspend fun setPastShowVisible(visible: Boolean): Result<ToggleResult> =
        runCatching {
            userDataSource.setPastShowVisible(visible).toDomain()
        }
}
