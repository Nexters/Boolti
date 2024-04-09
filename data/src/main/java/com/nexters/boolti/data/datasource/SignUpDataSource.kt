package com.nexters.boolti.data.datasource

import com.nexters.boolti.data.network.api.SignUpService
import com.nexters.boolti.data.network.response.SignUpResponse
import com.nexters.boolti.domain.request.SignUpRequest
import javax.inject.Inject

internal class SignUpDataSource @Inject constructor(
    private val signUpService: SignUpService,
) {
    suspend fun signUp(signUpRequest: SignUpRequest): Result<SignUpResponse> = runCatching {
        signUpService.signup(signUpRequest)
    }
}
