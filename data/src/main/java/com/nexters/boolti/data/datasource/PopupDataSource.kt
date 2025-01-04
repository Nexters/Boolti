package com.nexters.boolti.data.datasource

import com.nexters.boolti.data.network.api.PopupService
import com.nexters.boolti.data.network.response.PopupResponse
import javax.inject.Inject

internal class PopupDataSource @Inject constructor(
    private val service: PopupService
) {
    suspend fun getPopup(): PopupResponse = service.getPopup()
}