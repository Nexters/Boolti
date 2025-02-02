package com.nexters.boolti.data.datasource

import android.content.Context
import com.nexters.boolti.data.db.dataStore
import com.nexters.boolti.data.network.api.PopupService
import com.nexters.boolti.data.network.response.PopupResponse
import com.nexters.boolti.data.util.toLocalDate
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import java.time.LocalDate
import javax.inject.Inject

internal class PopupDataSource @Inject constructor(
    private val service: PopupService,
    context: Context,
) {
    private val dataStore = context.dataStore

    suspend fun getPopup(): PopupResponse = service.getPopup()

    fun shouldShowEvent(id: String): Flow<Boolean> = dataStore.data.map {
        LocalDate.now() > (it.dateHidingEvent?.get(id)?.toLocalDate() ?: LocalDate.MIN)
    }

    suspend fun hideEventToday(id: String) = dataStore.updateData { appSetting ->
        val newDateHidingEvent =
            appSetting.dateHidingEvent
                ?.filter { it.value == LocalDate.now().toEpochDay() }
                ?.toMutableMap()
                ?: mutableMapOf()
        newDateHidingEvent[id] = LocalDate.now().toEpochDay()
        appSetting.copy(dateHidingEvent = newDateHidingEvent)
    }
}