package com.nexters.boolti.data.repository

import com.nexters.boolti.data.datasource.SignUpDataSource
import com.nexters.boolti.data.datasource.TokenDataSource
import com.nexters.boolti.domain.repository.SignUpRepository
import com.nexters.boolti.domain.request.SignUpRequest
import javax.inject.Inject

class SignUpRepositoryImpl @Inject constructor(
    private val signUpDataSource: SignUpDataSource,
    private val tokenDataSource: TokenDataSource,
) : SignUpRepository {
    override suspend fun signUp(signUpRequest: SignUpRequest): Result<Unit> {
        return signUpDataSource.signUp(signUpRequest)
            .onSuccess { response ->
                tokenDataSource.saveTokens(response.accessToken, response.refreshToken)
            }
            .mapCatching { }
    }
}