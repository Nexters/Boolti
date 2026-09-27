package com.nexters.boolti.domain.repository

import com.nexters.boolti.domain.model.PlaceDetail
import com.nexters.boolti.domain.model.PlaceImage
import kotlinx.coroutines.flow.Flow

interface PlaceRepository {
    fun getPlace(placeId: String): Flow<PlaceDetail>
    fun getPlaceImages(placeId: String): Flow<List<PlaceImage>>
}
