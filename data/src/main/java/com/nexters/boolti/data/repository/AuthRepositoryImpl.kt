package com.nexters.boolti.data.repository

import com.nexters.boolti.data.datasource.AuthDataSource
import com.nexters.boolti.data.datasource.TokenDataSource
import com.nexters.boolti.domain.repository.AuthRepository
import com.nexters.boolti.domain.request.LoginRequest
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class AuthRepositoryImpl @Inject constructor(
    private val authDataSource: AuthDataSource,
    private val tokenDataSource: TokenDataSource,
) : AuthRepository {
    override suspend fun kakaoLogin(request: LoginRequest): Result<Boolean> {
        tokenDataSource.saveTokens("123123", "123123") // TODO Remove me
        return authDataSource.login(request)
            .onSuccess { response ->
                tokenDataSource.saveTokens(response.accessToken ?: "", response.refreshToken ?: "")
            }
            .mapCatching {
                !it.signupRequired
            }
    }

    override suspend fun logout() {
        authDataSource.logout()
    }

    override val loggedIn: Flow<Boolean>
        get() = authDataSource.loggedIn
}
