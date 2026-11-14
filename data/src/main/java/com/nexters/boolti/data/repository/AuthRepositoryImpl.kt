package com.nexters.boolti.data.repository

import com.nexters.boolti.common.tracker.AppTracker
import com.nexters.boolti.data.datasource.AuthDataSource
import com.nexters.boolti.data.datasource.AuthTokenDataSource
import com.nexters.boolti.data.datasource.DeviceTokenDataSource
import com.nexters.boolti.data.datasource.SignUpDataSource
import com.nexters.boolti.data.datasource.TokenDataSource
import com.nexters.boolti.data.datasource.UserDataSource
import com.nexters.boolti.data.network.response.LoginResponse
import com.nexters.boolti.domain.model.AccessToken
import com.nexters.boolti.domain.model.LoginUserState
import com.nexters.boolti.domain.model.TokenPairs
import com.nexters.boolti.domain.model.User
import com.nexters.boolti.domain.repository.AuthRepository
import com.nexters.boolti.domain.request.EditProfileRequest
import com.nexters.boolti.domain.request.LoginRequest
import com.nexters.boolti.domain.request.SignUpRequest
import com.nexters.boolti.domain.request.SignoutRequest
import com.nexters.boolti.domain.util.suspendRunCatching
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.map
import timber.log.Timber
import javax.inject.Inject

internal class AuthRepositoryImpl @Inject constructor(
    private val authDataSource: AuthDataSource,
    private val tokenDataSource: TokenDataSource,
    private val authTokenDataSource: AuthTokenDataSource,
    private val signUpDataSource: SignUpDataSource,
    private val userDateSource: UserDataSource,
    private val deviceTokenDataSource: DeviceTokenDataSource,
) : AuthRepository {
    override val loggedIn: Flow<Boolean> = authDataSource.loggedIn

    override val cachedUser: Flow<User.My?> = authDataSource.user.map { it?.toDomain() }

    override suspend fun kakaoLogin(request: LoginRequest): Result<LoginUserState> {
        return authDataSource.login(request).onSuccess { response ->
            val accessToken = response.accessToken
            val refreshToken = response.refreshToken
            // 가입이 필요한 사용자는 토큰이 없다. 토큰은 가입 후 signUp에서 저장한다
            if (accessToken == null || refreshToken == null) return@onSuccess

            tokenDataSource.saveTokens(accessToken, refreshToken)
            deviceTokenDataSource.sendFcmToken()
            // 캐시 실패로 로그인이 실패하면 안 된다. 비어 있으면 getUser()가 다시 받는다
            getUser(forceRefresh = true).onFailure { Timber.w(it) }
        }.mapCatching(LoginResponse::toDomain)
    }

    override suspend fun logout(): Result<Unit> = authDataSource.logout()

    override suspend fun signUp(signUpRequest: SignUpRequest): Result<Unit> {
        return signUpDataSource.signUp(signUpRequest).onSuccess { response ->
            tokenDataSource.saveTokens(response.accessToken, response.refreshToken)
            deviceTokenDataSource.sendFcmToken()
            // 캐시 실패로 가입이 실패하면 안 된다. 비어 있으면 getUser()가 다시 받는다
            getUser(forceRefresh = true).onFailure { Timber.w(it) }
        }.mapCatching { }
    }

    override suspend fun signout(request: SignoutRequest): Result<Unit> = runCatching {
        userDateSource.signout(request)
    }.onSuccess {
        authDataSource.localLogout()
    }

    override suspend fun getUser(forceRefresh: Boolean): Result<User.My> = suspendRunCatching {
        val cached = if (forceRefresh) null else cachedUser.first()
        cached ?: fetchAndCacheUser()
    }

    private suspend fun fetchAndCacheUser(): User.My {
        val response = checkNotNull(userDateSource.getUser()) { "내 정보 응답이 비어 있어요" }
        authDataSource.updateUser(response)
        AppTracker.identify(
            userId = response.id,
            properties = buildMap {
                response.userCode?.let { userCode -> put("userCode", userCode) }
                response.email?.let { email -> put($$"$email", email) }
                response.nickname?.let { nickname -> put($$"$name", nickname) }
            },
        )
        return response.toDomain()
    }

    override suspend fun sendFcmToken(): Result<Unit> = deviceTokenDataSource.sendFcmToken()

    override suspend fun editProfile(editProfileRequest: EditProfileRequest) =
        runCatching { userDateSource.edit(editProfileRequest) }
            .onSuccess { authDataSource.updateUser(it) }
            .mapCatching {}

    override fun getTokens(): Flow<TokenPairs> = authDataSource.getTokens().map {
        TokenPairs(it.first, it.second)
    }

    override fun refreshToken(): Flow<AccessToken> = flow {
        authTokenDataSource.getNewAccessToken()?.let {
            emit(AccessToken(it))
        }
    }
}
