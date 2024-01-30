package com.nexters.boolti.domain.repository

import com.nexters.boolti.domain.request.SignUpRequest

interface SignUpRepository {
    suspend fun signUp(signUpRequest: SignUpRequest): Result<Unit>
}