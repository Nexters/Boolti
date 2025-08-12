package com.nexters.boolti.domain.repository

import com.nexters.boolti.domain.model.ToggleResult

interface UserConfigRepository {
    suspend fun setUpcomingShowVisible(visible: Boolean): Result<ToggleResult>
    suspend fun setPastShowVisible(visible: Boolean): Result<ToggleResult>
}
