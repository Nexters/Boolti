package com.nexters.boolti.domain.repository

import com.nexters.boolti.domain.model.Place
import com.nexters.boolti.domain.model.PlaceImage
import kotlinx.coroutines.flow.Flow

interface PlaceRepository {
    fun getPlace(placeId: String): Flow<Place>

    fun getPlaceImages(placeId: String): Flow<List<PlaceImage>>
}
