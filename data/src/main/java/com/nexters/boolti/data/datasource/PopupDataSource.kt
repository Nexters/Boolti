package com.nexters.boolti.data.datasource

import android.content.Context
import com.nexters.boolti.data.network.api.PopupService
import com.nexters.boolti.data.network.response.PopupResponse
import javax.inject.Inject

internal class PopupDataSource @Inject constructor(
    private val service: PopupService,
    private val context: Context,
) {
    suspend fun getPopup(): PopupResponse = service.getPopup()

    suspend fun shouldShowEvent(): Boolean {
        // todo
        return true
    }

    suspend fun hideEventToday() {
        // todo
    }
}