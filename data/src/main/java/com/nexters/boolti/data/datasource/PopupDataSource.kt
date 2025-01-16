package com.nexters.boolti.data.datasource

import android.content.Context
import com.nexters.boolti.data.db.dataStore
import com.nexters.boolti.data.network.api.PopupService
import com.nexters.boolti.data.network.response.PopupResponse
import com.nexters.boolti.data.util.toLocalDate
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import java.time.LocalDate
import java.time.LocalDateTime
import javax.inject.Inject

internal class PopupDataSource @Inject constructor(
    private val service: PopupService,
    context: Context,
) {
    private val dataStore = context.dataStore

    suspend fun getPopup(): PopupResponse = service.getPopup()

    fun shouldShowEvent(): Flow<Boolean> = dataStore.data.map {
        it.dateHidingEvent == null || LocalDate.now() > it.dateHidingEvent.toLocalDate()
    }


    suspend fun hideEventToday() {
        dataStore.updateData {
            it.copy(dateHidingEvent = LocalDateTime.now().toString())
        }
    }
}