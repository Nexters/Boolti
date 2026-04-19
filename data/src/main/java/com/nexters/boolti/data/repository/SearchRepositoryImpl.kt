package com.nexters.boolti.data.repository

import com.nexters.boolti.data.datasource.SearchDataSource
import com.nexters.boolti.domain.model.NewShowsAndRisingKeywords
import com.nexters.boolti.domain.model.PagingData
import com.nexters.boolti.domain.model.Show
import com.nexters.boolti.domain.model.User
import com.nexters.boolti.domain.model.Venue
import com.nexters.boolti.domain.repository.SearchRepository
import com.nexters.boolti.domain.util.suspendRunCatching
import javax.inject.Inject

internal class SearchRepositoryImpl @Inject constructor(
    private val searchDataSource: SearchDataSource,
) : SearchRepository {
    override suspend fun getNewShowsAndRisingKeywords(): Result<NewShowsAndRisingKeywords> = suspendRunCatching {
        searchDataSource.getOverview()
    }

    override suspend fun getRecommendKeyword(searchKeyword: String): Result<List<String>> = suspendRunCatching {
        searchDataSource.getAutoCompleteKeywords(searchKeyword, 10)
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
        searchDataSource.getProfiles(keyword, page, 10)
    }

    // TODO: API 연동 시 searchDataSource.getVenues(keyword, page, 10) 로 교체
    override suspend fun searchVenues(
        keyword: String,
        page: Int,
    ): Result<PagingData<Venue>> = suspendRunCatching {
        PagingData(
            items = emptyList(),
            currentPage = page,
            pageSize = 10,
            totalElements = 0L,
            totalPages = 0,
            hasNext = false,
        )
    }
}
