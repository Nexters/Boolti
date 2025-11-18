package com.nexters.boolti.data.datasource

import com.nexters.boolti.data.db.dao.SearchHistoryDao
import com.nexters.boolti.data.db.entity.SearchHistoryEntity
import kotlinx.coroutines.flow.Flow
import java.time.LocalDateTime
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
internal class LocalSearchHistoryDataSource @Inject constructor(
    private val searchHistoryDao: SearchHistoryDao,
) {
    suspend fun insert(keyword: String) {
        searchHistoryDao.insert(
            SearchHistoryEntity(
                keyword = keyword,
                searchedAt = LocalDateTime.now()
            )
        )
    }

    fun getRecentSearches(count: Int): Flow<List<SearchHistoryEntity>> {
        return searchHistoryDao.getRecentSearches(count)
    }

    suspend fun deleteByKeyword(keyword: String) {
        searchHistoryDao.deleteByKeyword(keyword)
    }

    suspend fun deleteAll() {
        searchHistoryDao.deleteAll()
    }
}
