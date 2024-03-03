package com.nexters.boolti.data.datasource

import com.nexters.boolti.data.network.api.ShowService
import com.nexters.boolti.data.network.response.ShowDetailResponse
import com.nexters.boolti.data.network.response.ShowResponse
import javax.inject.Inject

internal class ShowDataSource @Inject constructor(
    private val showService: ShowService,
) {
    suspend fun search(keyword: String): Result<List<ShowResponse>> = runCatching {
        showService.search(keyword)
    }

    suspend fun findShowById(id: String): Result<ShowDetailResponse> = runCatching {
        showService.findShowById(id)
    }
}
