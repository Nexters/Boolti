package com.nexters.boolti.domain.repository

import com.nexters.boolti.domain.model.Place

interface PlaceRepository {
    suspend fun getPlace(placeId: String): Result<Place>
}
