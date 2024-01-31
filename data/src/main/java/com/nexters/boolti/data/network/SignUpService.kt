package com.nexters.boolti.data.network

import com.nexters.boolti.data.network.response.SignUpResponse
import com.nexters.boolti.domain.request.SignUpRequest
import retrofit2.http.Body
import retrofit2.http.POST

interface SignUpService {
    @POST("/app/papi/v1/signup/sns")
    suspend fun signup(@Body request: SignUpRequest): SignUpResponse
}