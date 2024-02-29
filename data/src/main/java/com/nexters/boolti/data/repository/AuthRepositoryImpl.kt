package com.nexters.boolti.data.repository

import com.nexters.boolti.data.datasource.AuthDataSource
import com.nexters.boolti.data.datasource.SignUpDataSource
import com.nexters.boolti.data.datasource.TokenDataSource
import com.nexters.boolti.data.datasource.UserDataSource
import com.nexters.boolti.data.network.response.LoginResponse
import com.nexters.boolti.domain.model.LoginUserState
import com.nexters.boolti.domain.model.User
import com.nexters.boolti.domain.repository.AuthRepository
import com.nexters.boolti.domain.request.LoginRequest
import com.nexters.boolti.domain.request.SignUpRequest
import com.nexters.boolti.domain.request.SignoutRequest
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject

class AuthRepositoryImpl @Inject constructor(
    private val authDataSource: AuthDataSource,
    private val tokenDataSource: TokenDataSource,
    private val signUpDataSource: SignUpDataSource,
    private val userDateSource: UserDataSource,
) : AuthRepository {
    override val loggedIn: Flow<Boolean>
        get() = authDataSource.loggedIn

    override val cachedUser: Flow<User?>
        get() = authDataSource.user.map { it?.toDomain() }

    override suspend fun kakaoLogin(request: LoginRequest): Result<LoginUserState> {
        return authDataSource.login(request)
            .onSuccess { response ->
                tokenDataSource.saveTokens(response.accessToken ?: "", response.refreshToken ?: "")
            }
            .mapCatching(LoginResponse::toDomain)
    }

    override suspend fun logout(): Result<Unit> = authDataSource.logout()

    override suspend fun signUp(signUpRequest: SignUpRequest): Result<Unit> {
        return signUpDataSource.signUp(signUpRequest)
            .onSuccess { response ->
                tokenDataSource.saveTokens(response.accessToken, response.refreshToken)
            }
            .mapCatching { }
    }

    override suspend fun signout(request: SignoutRequest): Result<Unit> = runCatching {
        userDateSource.signout(request)
    }.onSuccess {
        authDataSource.localLogout()
    }

    override fun getUserAndCache(): Flow<User> = flow {
        val response = userDateSource.getUser()
        authDataSource.updateUser(response)
        emit(response.toDomain())
    }
}
