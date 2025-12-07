package com.nexters.boolti.data.network.api

import com.nexters.boolti.data.network.response.MemberResponse
import com.nexters.boolti.data.network.response.PagingResponse
import com.nexters.boolti.data.network.response.SearchOverviewResponse
import com.nexters.boolti.data.network.response.ShowResponse
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
    ): PagingResponse<ShowResponse>

    @GET("/app/papi/v1/shows/artists")
    suspend fun requestProfiles(
        @Query("keyword")
        keyword: String,
        @Query("page")
        page: Int,
        @Query("size")
        size: Int,
    ): PagingResponse<MemberResponse>

    @GET("/app/papi/v1/shows/autocomplete")
    suspend fun requestAutoCompleteKeywords(
        @Query("keyword")
        keyword: String,
        @Query("limit")
        limit: Int,
    ): List<String>
}
