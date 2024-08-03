package com.nexters.boolti.data.datasource

import com.nexters.boolti.data.network.api.UserService
import com.nexters.boolti.data.network.response.UserResponse
import com.nexters.boolti.domain.request.SignoutRequest
import javax.inject.Inject

internal class UserDataSource @Inject constructor(
    private val userService: UserService,
) {
    suspend fun getUser(): UserResponse? {
        val response = userService.getUser()
        return if (response.isSuccessful) response.body() else null
    }

    suspend fun signout(request: SignoutRequest) = userService.signout(request)
}
