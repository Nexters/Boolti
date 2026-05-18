package com.nexters.boolti.domain.repository

import com.nexters.boolti.domain.model.Place
import kotlinx.coroutines.flow.Flow

interface PlaceRepository {
    fun getPlace(placeId: String): Flow<Place>
}
