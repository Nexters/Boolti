package com.nexters.boolti.data.datasource

import com.nexters.boolti.data.network.api.UserService
import com.nexters.boolti.data.network.response.UserResponse
import javax.inject.Inject

class UserDataSource @Inject constructor(
    private val userService: UserService,
) {
    suspend fun getUser(): UserResponse = userService.getUser()
}