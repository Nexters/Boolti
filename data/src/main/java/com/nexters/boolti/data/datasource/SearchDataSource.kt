package com.nexters.boolti.data.datasource

import com.nexters.boolti.data.network.api.SearchService
import com.nexters.boolti.data.network.response.MemberResponse
import com.nexters.boolti.data.network.response.ShowResponse
import com.nexters.boolti.data.network.response.toNewShowsAndRisingKeywords
import com.nexters.boolti.data.util.getPagingData
import com.nexters.boolti.data.util.toPagingData
import com.nexters.boolti.domain.model.NewShowsAndRisingKeywords
import com.nexters.boolti.domain.model.PagingData
import com.nexters.boolti.domain.model.Show
import com.nexters.boolti.domain.model.map
import javax.inject.Inject

internal class SearchDataSource @Inject constructor(
    private val searchService: SearchService,
) {
    suspend fun getOverview(): NewShowsAndRisingKeywords = searchService.requestOverview().toNewShowsAndRisingKeywords()

    suspend fun getShows(
        keyword: String,
        page: Int,
        size: Int,
    ): PagingData<Show> {
        return searchService.requestShows(keyword, page, size)
            .toPagingData(ShowResponse.serializer())
            .map { it.toDomain() }
    }

    suspend fun getProfiles(
        keyword: String,
        page: Int,
        size: Int,
    ): PagingData<MemberResponse> = getPagingData { searchService.requestProfiles(keyword, page, size) }
}
