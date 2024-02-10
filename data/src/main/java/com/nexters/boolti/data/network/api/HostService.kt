package com.nexters.boolti.data.network.api

import com.nexters.boolti.data.network.response.HostedShowDto
import com.nexters.boolti.domain.request.QrScanRequest
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST

interface HostService {
    @GET("/app/api/v1/host/shows")
    suspend fun getHostedShows(): List<HostedShowDto>

    @POST("/app/api/v1/ticket/entrance")
    suspend fun requestEntrance(
        @Body qrScanRequest: QrScanRequest,
    ): Response<Boolean>
}
