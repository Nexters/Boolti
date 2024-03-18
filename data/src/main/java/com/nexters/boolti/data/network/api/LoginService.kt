package com.nexters.boolti.data.network.api

import com.nexters.boolti.data.network.request.RefreshRequest
import com.nexters.boolti.data.network.response.LoginResponse
import com.nexters.boolti.data.network.response.SignUpResponse
import com.nexters.boolti.domain.request.LoginRequest
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.POST

internal interface LoginService {
    @POST("/app/papi/v1/login/kakao")
    suspend fun kakaoLogin(@Body request: LoginRequest): LoginResponse

    @POST("/app/v1/logout")
    suspend fun logout()

    @POST("/app/papi/v1/login/refresh")
    suspend fun refresh(@Body refreshToken: RefreshRequest): SignUpResponse
}
