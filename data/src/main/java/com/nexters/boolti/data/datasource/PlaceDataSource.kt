package com.nexters.boolti.data.datasource

import com.nexters.boolti.data.network.api.PlaceService
import com.nexters.boolti.data.network.response.PlaceDetailResponse
import javax.inject.Inject

internal class PlaceDataSource @Inject constructor(
    private val placeService: PlaceService,
) {
    suspend fun getPlace(placeId: String): PlaceDetailResponse {
        return placeService.getPlace(placeId)
    }
}
