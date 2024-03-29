package com.nexters.boolti.data.network.api

import com.nexters.boolti.data.network.response.HostedShowDto
import com.nexters.boolti.data.network.response.ManagerCodeDto
import com.nexters.boolti.domain.request.ManagerCodeRequest
import com.nexters.boolti.domain.request.QrScanRequest
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Path

internal interface HostService {
    @GET("/app/api/v1/host/shows")
    suspend fun getHostedShows(): List<HostedShowDto>

    @POST("/app/api/v1/ticket/entrance")
    suspend fun requestEntrance(
        @Body qrScanRequest: QrScanRequest,
    ): Response<Boolean>

    @POST("/app/api/v1/ticket/entrance/manager")
    suspend fun requestEntranceWithManagerCode(
        @Body request: ManagerCodeRequest,
    ): Response<Boolean>

    @GET("/app/api/v1/host/manager-code/{showId}")
    suspend fun getManagerCode(
        @Path("showId") showId: String,
    ): ManagerCodeDto
}
