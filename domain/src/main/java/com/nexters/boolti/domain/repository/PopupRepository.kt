package com.nexters.boolti.domain.repository

import com.nexters.boolti.domain.model.Popup
import kotlinx.coroutines.flow.Flow

interface PopupRepository {
    fun shouldShowEvent(id: String): Flow<Boolean>
    fun hideEventToday(id: String): Flow<Unit>
    fun getPopup(view: String): Flow<Popup>
}