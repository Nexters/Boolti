package com.nexters.boolti.data.repository

import com.nexters.boolti.data.datasource.PopupDataSource
import com.nexters.boolti.domain.model.Popup
import com.nexters.boolti.domain.model.PopupResult
import com.nexters.boolti.domain.repository.PopupRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flow
import javax.inject.Inject

internal class PopupRepositoryImpl @Inject constructor(
    private val dataSource: PopupDataSource,
) : PopupRepository {

    override fun hideEventToday(id: String): Flow<Unit> = flow {
        dataSource.hideEventToday(id)
    }

    override suspend fun getPopup(view: String): PopupResult {
        val popup = dataSource.getPopup(view)?.toDomain() ?: return PopupResult.None
        if (popup is Popup.Event && !dataSource.shouldShowEvent(popup.id).first()) {
            return PopupResult.Hidden(popup)
        }
        return PopupResult.Show(popup)
    }

    override fun shouldShowNaverMapDialog() = dataSource.shouldShowNaverMapDialog()

    override suspend fun doNotShowNaverMapPopupAnyMore() {
        dataSource.doNotShowNaverMapPopupAnyMore()
    }
}
