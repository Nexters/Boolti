package com.nexters.boolti.domain.repository

import com.nexters.boolti.domain.model.Show
import com.nexters.boolti.domain.model.ShowDetail

interface ShowRepository {
    suspend fun search(keyword: String): Result<List<Show>>
    suspend fun searchById(id: String): Result<ShowDetail>
}