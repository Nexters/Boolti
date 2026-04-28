package com.nexters.boolti.data.network.api

import com.nexters.boolti.data.network.response.PlaceResponse
import retrofit2.http.GET
import retrofit2.http.Path

internal interface PlaceService {
    @GET("/app/papi/v1/places/{placeId}")
    suspend fun getPlace(@Path("placeId") placeId: String): PlaceResponse
}
