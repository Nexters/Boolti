package com.nexters.boolti.data.repository

import com.nexters.boolti.data.datasource.AuthDataSource
import com.nexters.boolti.data.datasource.SignUpDataSource
import com.nexters.boolti.data.datasource.TokenDataSource
import com.nexters.boolti.data.datasource.UserDataSource
import com.nexters.boolti.domain.model.User
import com.nexters.boolti.domain.repository.AuthRepository
import com.nexters.boolti.domain.request.LoginRequest
import com.nexters.boolti.domain.request.SignUpRequest
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
    override val cachedUser: Flow<User>
        get() = authDataSource.data.map {
            User(
                id = it.userId ?: "",
                nickname = it.nickname ?: "",
                email = it.email ?: "",
                photo = null,
            )
        }

    override suspend fun kakaoLogin(request: LoginRequest): Result<Boolean> {
        return authDataSource.login(request)
            .onSuccess { response ->
                tokenDataSource.saveTokens(response.accessToken ?: "", response.refreshToken ?: "")
            }
            .mapCatching {
                !it.signUpRequired
            }
    }

    override suspend fun logout(): Result<Unit> = authDataSource.logout()

    override suspend fun signUp(signUpRequest: SignUpRequest): Result<Unit> {
        return signUpDataSource.signUp(signUpRequest)
            .onSuccess { response ->
                tokenDataSource.saveTokens(response.accessToken, response.refreshToken)
            }
            .mapCatching { }
    }

    override fun getUserAndCache(): Flow<User> = flow {
        val response = userDateSource.getUser()
        authDataSource.updateUser(response)
        emit(response.toDomain())
    }
}
