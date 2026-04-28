package com.nexters.boolti.data.repository

import com.nexters.boolti.data.datasource.PlaceDataSource
import com.nexters.boolti.domain.model.Place
import com.nexters.boolti.domain.repository.PlaceRepository
import javax.inject.Inject

internal class PlaceRepositoryImpl @Inject constructor(
    private val placeDataSource: PlaceDataSource,
) : PlaceRepository {
    override suspend fun getPlace(placeId: String): Result<Place> {
        return placeDataSource.getPlace(placeId).map { it.toDomain() }
    }
}
