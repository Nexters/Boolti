package com.nexters.boolti.domain.repository

import com.nexters.boolti.domain.model.Popup
import kotlinx.coroutines.flow.Flow

interface PopupRepository {
    fun getPopup(): Flow<Popup>
}