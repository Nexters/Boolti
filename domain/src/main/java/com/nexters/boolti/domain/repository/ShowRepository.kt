package com.nexters.boolti.domain.repository

import com.nexters.boolti.domain.model.Show

interface ShowRepository {
    suspend fun search(keyword: String): Result<List<Show>>
}