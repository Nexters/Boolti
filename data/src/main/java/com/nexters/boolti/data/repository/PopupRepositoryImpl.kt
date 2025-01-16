package com.nexters.boolti.data.repository

import com.nexters.boolti.data.datasource.PopupDataSource
import com.nexters.boolti.domain.repository.PopupRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import javax.inject.Inject

internal class PopupRepositoryImpl @Inject constructor(
    private val dataSource: PopupDataSource,
) : PopupRepository {

    override fun shouldShowEvent(): Flow<Boolean> = flow {
        emit(true) // TODO : preference에서 가져오기
    }

    override fun hideEventToday(): Flow<Unit> = flow {
        // TODO : preference에 저장
    }

    override fun getPopup() = flow {
        emit(dataSource.getPopup().toDomain())
    }
}