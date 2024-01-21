package com.nexters.boolti.data.network

import com.nexters.boolti.data.network.response.LoginResponse
import com.nexters.boolti.domain.request.LoginRequest
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST

interface ApiService {
    @POST("/app/papi/v1/login/kakao")
    suspend fun kakaoLogin(@Body request: LoginRequest): LoginResponse
}
