package com.nexters.boolti.data.datasource

import com.nexters.boolti.data.network.api.PlaceService
import com.nexters.boolti.data.network.response.PlaceImageListResponse
import com.nexters.boolti.data.network.response.ConcertHallProfileResponse
import javax.inject.Inject

internal class PlaceDataSource @Inject constructor(
    private val placeService: PlaceService,
) {
    suspend fun getPlace(placeId: String): ConcertHallProfileResponse {
        return placeService.getPlace(placeId)
    }

    suspend fun getPlaceImages(placeId: String): PlaceImageListResponse {
        return placeService.getPlaceImages(placeId)
    }
}
