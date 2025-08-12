package com.nexters.boolti.data.network.api

import com.nexters.boolti.data.network.request.SetVisibleRequest
import com.nexters.boolti.data.network.response.ToggleResultDto
import com.nexters.boolti.data.network.response.UserResponse
import com.nexters.boolti.domain.request.EditProfileRequest
import com.nexters.boolti.domain.request.SignoutRequest
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.HTTP
import retrofit2.http.PATCH

internal interface UserService {
    @GET("/app/api/v2/user")
    suspend fun getUser(): Response<UserResponse>

    @HTTP(method = "DELETE", path = "/app/api/v1/user", hasBody = true)
    suspend fun signout(
        @Body request: SignoutRequest,
    )

    @HTTP(method = "PATCH", path = "/app/api/v1/user", hasBody = true)
    suspend fun editProfile(
        @Body request: EditProfileRequest,
    ): UserResponse

    @PATCH("/app/api/v1/user/upcoming-visible")
    suspend fun setUpcomingShowVisible(
        @Body request: SetVisibleRequest,
    ): ToggleResultDto

    @PATCH("/app/api/v1/user/past-visible")
    suspend fun setPastShowVisible(
        @Body request: SetVisibleRequest,
    ): ToggleResultDto
}
