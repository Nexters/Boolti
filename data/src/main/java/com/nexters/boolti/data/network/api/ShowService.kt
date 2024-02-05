package com.nexters.boolti.data.network.api

import com.nexters.boolti.data.network.response.ShowResponse
import retrofit2.http.GET
import retrofit2.http.Query

interface ShowService {
    @GET("/app/papi/v1/shows/search")
    suspend fun search(@Query("nameLike") keyword: String): List<ShowResponse>
}