package com.nexters.boolti.data.repository

import com.nexters.boolti.data.datasource.PlaceDataSource
import com.nexters.boolti.domain.model.Place
import com.nexters.boolti.domain.repository.PlaceRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import javax.inject.Inject

internal class PlaceRepositoryImpl @Inject constructor(
    private val placeDataSource: PlaceDataSource,
) : PlaceRepository {
    override fun getPlace(placeId: String): Flow<Place> = flow {
        emit(placeDataSource.getPlace(placeId).toDomain())
    }
}
