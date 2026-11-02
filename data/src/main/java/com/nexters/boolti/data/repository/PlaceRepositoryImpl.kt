package com.nexters.boolti.data.repository

import com.nexters.boolti.data.cache.CacheKeys
import com.nexters.boolti.data.cache.CacheStore
import com.nexters.boolti.data.datasource.PlaceDataSource
import com.nexters.boolti.domain.model.PlaceDetail
import com.nexters.boolti.domain.model.PlaceImage
import com.nexters.boolti.domain.repository.PlaceRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import javax.inject.Inject

internal class PlaceRepositoryImpl @Inject constructor(
    private val placeDataSource: PlaceDataSource,
    private val cacheStore: CacheStore,
) : PlaceRepository {
    override fun getPlace(placeId: String): Flow<PlaceDetail> = flow {
        emit(placeDataSource.getPlace(placeId).toDomain())
    }

    override fun getPlaceImages(placeId: String): Flow<List<PlaceImage>> = flow {
        emit(cacheStore.getOrFetch(CacheKeys.placeImages(placeId)) { placeDataSource.getPlaceImages(placeId).toDomain() })
    }
}
