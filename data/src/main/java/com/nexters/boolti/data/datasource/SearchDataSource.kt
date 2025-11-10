package com.nexters.boolti.data.datasource

import com.nexters.boolti.data.network.api.SearchService
import com.nexters.boolti.data.network.response.toNewShowsAndRisingKeywords
import com.nexters.boolti.domain.model.NewShowsAndRisingKeywords
import javax.inject.Inject

internal class SearchDataSource @Inject constructor(
    private val searchService: SearchService,
) {
    suspend fun getOverview(): NewShowsAndRisingKeywords = searchService.requestOverview().toNewShowsAndRisingKeywords()
}
