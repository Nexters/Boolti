package com.nexters.boolti.data.repository

import com.nexters.boolti.data.datasource.SearchDataSource
import com.nexters.boolti.domain.model.NewShowsAndRisingKeywords
import com.nexters.boolti.domain.model.PagingData
import com.nexters.boolti.domain.model.Show
import com.nexters.boolti.domain.model.User
import com.nexters.boolti.domain.model.map
import com.nexters.boolti.domain.repository.SearchRepository
import com.nexters.boolti.domain.util.suspendRunCatching
import javax.inject.Inject

internal class SearchRepositoryImpl @Inject constructor(
    private val searchDataSource: SearchDataSource,
) : SearchRepository {
    override suspend fun getNewShowsAndRisingKeywords(): Result<NewShowsAndRisingKeywords> = suspendRunCatching {
        searchDataSource.getOverview()
    }

    override suspend fun getRecommendKeyword(searchKeyword: String): Result<List<String>> {
        val recommendations = when {
            searchKeyword.contains("불티", ignoreCase = true) -> listOf("불티 밴드", "불티 콘서트", "불티 공연")
            searchKeyword.contains("재즈", ignoreCase = true) -> listOf("재즈 콘서트", "재즈 바", "재즈 밴드")
            searchKeyword.contains("클래식", ignoreCase = true) -> listOf("클래식 기타", "클래식 콘서트", "클래식 음악회")
            searchKeyword.contains("밴드", ignoreCase = true) -> listOf("밴드 공연", "밴드 콘서트", "인디 밴드")
            else -> listOf("${searchKeyword} 공연", "${searchKeyword} 콘서트", "${searchKeyword} 티켓")
        }

        return Result.success(recommendations)
    }

    override suspend fun searchShows(
        keyword: String,
        page: Int
    ): Result<PagingData<Show>> = suspendRunCatching {
        searchDataSource.getShows(keyword, page, 10)
    }

    override suspend fun searchProfiles(
        keyword: String,
        page: Int
    ): Result<PagingData<User.Others>> = suspendRunCatching {
        searchDataSource.getProfiles(keyword, page, 10).map { it.toDomain() }
    }
}
