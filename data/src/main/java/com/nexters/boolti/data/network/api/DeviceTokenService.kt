package com.nexters.boolti.data.network.api


import com.nexters.boolti.data.network.request.DeviceTokenRequest
import com.nexters.boolti.data.network.response.DeviceTokenResponse
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.POST

internal interface DeviceTokenService {
    @POST("/app/papi/v1/device-token")
    suspend fun postFcmToken(@Body request: DeviceTokenRequest): Response<DeviceTokenResponse>
}
