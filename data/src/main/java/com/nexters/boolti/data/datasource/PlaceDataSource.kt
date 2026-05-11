package com.nexters.boolti.data.datasource

import com.nexters.boolti.data.network.api.PlaceService
import com.nexters.boolti.data.network.response.ConcertHallProfileResponse
import javax.inject.Inject

internal class PlaceDataSource @Inject constructor(
    private val placeService: PlaceService,
) {
    suspend fun getPlace(placeId: String): Result<ConcertHallProfileResponse> = runCatching {
        placeService.getPlace(placeId)
    }
}
