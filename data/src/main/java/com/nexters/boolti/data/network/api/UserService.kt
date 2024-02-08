package com.nexters.boolti.data.network.api

import com.nexters.boolti.data.network.response.UserResponse
import retrofit2.http.GET

interface UserService {
    @GET("/app/api/v1/user")
    suspend fun getUser(): UserResponse
}