package com.nexters.boolti.domain.repository

import com.nexters.boolti.domain.model.SearchHistory
import kotlinx.coroutines.flow.Flow

interface SearchHistoryRepository {
    suspend fun saveSearchHistory(keyword: String)
    fun getRecentSearchHistories(count: Int): Flow<List<SearchHistory>>
    suspend fun deleteSearchHistory(keyword: String)
    suspend fun clearSearchHistories()
}
