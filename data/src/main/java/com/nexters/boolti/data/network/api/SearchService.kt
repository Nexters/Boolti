package com.nexters.boolti.data.network.api

import com.nexters.boolti.data.network.response.SearchOverviewResponse
import retrofit2.http.GET

internal interface SearchService {
    @GET("/app/papi/v1/search/overview")
    suspend fun requestOverview(): SearchOverviewResponse
}
