package com.nexters.boolti.domain.repository

import kotlinx.coroutines.flow.Flow

interface ConfigRepository {
    fun shouldUpdate(): Flow<Boolean>
}
