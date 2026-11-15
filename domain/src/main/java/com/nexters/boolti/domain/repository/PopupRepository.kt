package com.nexters.boolti.domain.repository

import com.nexters.boolti.domain.model.PopupResult
import kotlinx.coroutines.flow.Flow

interface PopupRepository {
    fun hideEventToday(id: String): Flow<Unit>
    suspend fun getPopup(view: String): PopupResult
    fun shouldShowNaverMapDialog(): Flow<Boolean>
    suspend fun doNotShowNaverMapPopupAnyMore()
}
