package com.nexters.boolti.data.repository

import com.nexters.boolti.data.datasource.LocalSearchHistoryDataSource
import com.nexters.boolti.domain.model.SearchHistory
import com.nexters.boolti.domain.repository.SearchHistoryRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject

internal class SearchHistoryRepositoryImpl @Inject constructor(
    private val localSearchHistoryDataSource: LocalSearchHistoryDataSource,
) : SearchHistoryRepository {
    override suspend fun saveSearchHistory(keyword: String) {
        localSearchHistoryDataSource.insert(keyword)
    }

    override fun getRecentSearchHistories(count: Int): Flow<List<SearchHistory>> {
        return localSearchHistoryDataSource.getRecentSearches(count).map { entities ->
            entities.map { entity ->
                SearchHistory(
                    keyword = entity.keyword,
                    searchedAt = entity.searchedAt
                )
            }
        }
    }

    override suspend fun deleteSearchHistory(keyword: String) {
        localSearchHistoryDataSource.deleteByKeyword(keyword)
    }

    override suspend fun clearSearchHistories() {
        localSearchHistoryDataSource.deleteAll()
    }
}
