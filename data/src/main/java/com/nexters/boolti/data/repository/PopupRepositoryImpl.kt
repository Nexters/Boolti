package com.nexters.boolti.data.repository

import com.nexters.boolti.data.datasource.PopupDataSource
import com.nexters.boolti.domain.repository.PopupRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import javax.inject.Inject

internal class PopupRepositoryImpl @Inject constructor(
    private val dataSource: PopupDataSource,
) : PopupRepository {

    override fun shouldShowEvent(id: String): Flow<Boolean> = dataSource.shouldShowEvent(id)

    override fun hideEventToday(id: String): Flow<Unit> = flow {
        dataSource.hideEventToday(id)
    }

    override fun getPopup(view: String) = flow {
        emit(dataSource.getPopup(view).toDomain())
    }
}