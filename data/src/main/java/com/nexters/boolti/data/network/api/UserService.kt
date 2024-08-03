package com.nexters.boolti.data.network.api

import com.nexters.boolti.data.network.response.UserResponse
import com.nexters.boolti.domain.request.SignoutRequest
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.HTTP

internal interface UserService {
    @GET("/app/api/v1/user")
    suspend fun getUser(): Response<UserResponse>

    @HTTP(method = "DELETE", path = "/app/api/v1/user", hasBody = true)
    suspend fun signout(
        @Body request: SignoutRequest,
    )
}
