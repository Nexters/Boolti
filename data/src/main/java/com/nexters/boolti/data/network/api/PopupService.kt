package com.nexters.boolti.data.network.api

import com.nexters.boolti.data.network.response.PopupResponse
import retrofit2.http.GET
import retrofit2.http.Path

internal interface PopupService {
    @GET("/app/papi/v1/popup/{view}")
    suspend fun getPopup(@Path("view") view: String): PopupResponse
}