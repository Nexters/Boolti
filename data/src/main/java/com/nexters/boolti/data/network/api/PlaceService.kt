package com.nexters.boolti.data.network.api

import com.nexters.boolti.data.network.response.ConcertHallImageListResponse
import com.nexters.boolti.data.network.response.ConcertHallProfileResponse
import retrofit2.http.GET
import retrofit2.http.Path

internal interface PlaceService {
    @GET("/app/papi/v1/concert-halls/{id}")
    suspend fun getPlace(@Path("id") placeId: String): ConcertHallProfileResponse

    @GET("/app/papi/v1/concert-halls/{id}/images")
    suspend fun getPlaceImages(@Path("id") placeId: String): ConcertHallImageListResponse
}
