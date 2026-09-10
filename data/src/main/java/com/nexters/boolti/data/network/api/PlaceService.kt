package com.nexters.boolti.data.network.api

import com.nexters.boolti.data.network.response.PlaceDetailResponse
import retrofit2.http.GET
import retrofit2.http.Path

internal interface PlaceService {
    @GET("/app/papi/v1/concert-halls/{id}")
    suspend fun getPlace(@Path("id") placeId: String): PlaceDetailResponse
}
