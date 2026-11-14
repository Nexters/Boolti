package com.nexters.boolti.data.datasource

import com.nexters.boolti.data.network.api.ShowService
import com.nexters.boolti.data.network.response.CastTeamsDto
import com.nexters.boolti.data.network.response.ShowDetailResponse
import com.nexters.boolti.data.network.response.ShowResponse
import com.nexters.boolti.domain.util.suspendRunCatching
import javax.inject.Inject

internal class ShowDataSource @Inject constructor(
    private val showService: ShowService,
) {
    suspend fun search(keyword: String): Result<List<ShowResponse>> = suspendRunCatching {
        showService.search(keyword)
    }

    suspend fun findShowById(id: String): Result<ShowDetailResponse> = suspendRunCatching {
        showService.findShowById(id)
    }

    suspend fun requestCastTeams(id: String): Result<List<CastTeamsDto>> = suspendRunCatching {
        showService.requestCastTeams(id)
    }
}
