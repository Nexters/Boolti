package com.nexters.boolti.data.network.api

import com.nexters.boolti.data.network.response.PopupResponse
import retrofit2.http.GET

internal interface PopupService {
    @GET("/app/api/v1/popup")
    suspend fun getPopup(): PopupResponse
}