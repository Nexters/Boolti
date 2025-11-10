package com.nexters.boolti.data.network.api

import com.nexters.boolti.data.network.response.SearchOverviewResponse
import kotlinx.serialization.json.JsonObject
import retrofit2.http.GET
import retrofit2.http.Query

internal interface SearchService {
    @GET("/app/papi/v1/search/overview")
    suspend fun requestOverview(): SearchOverviewResponse

    @GET("/app/papi/v1/shows")
    suspend fun requestShows(
        @Query("keyword")
        keyword: String,
        @Query("page")
        page: Int,
        @Query("size")
        size: Int,
    ): JsonObject
}
