package com.nexters.boolti.data.repository

import com.nexters.boolti.data.datasource.PopupDataSource
import com.nexters.boolti.domain.repository.PopupRepository
import kotlinx.coroutines.flow.flow
import javax.inject.Inject

internal class PopupRepositoryImpl @Inject constructor(
    private val dataSource: PopupDataSource,
) : PopupRepository {
    override fun getPopup() = flow {
        emit(dataSource.getPopup().toDomain())
    }
}