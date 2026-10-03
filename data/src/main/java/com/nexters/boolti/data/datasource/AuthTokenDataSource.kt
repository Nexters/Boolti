package com.nexters.boolti.data.datasource

import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import retrofit2.HttpException
import timber.log.Timber
import java.io.IOException
import javax.inject.Inject

internal class AuthTokenDataSource @Inject constructor(
    private val authDataSource: AuthDataSource,
    private val tokenDataSource: TokenDataSource,
) {
    // DataSourceModule에서 @Singleton으로 제공되므로 앱 전체가 락 하나를 공유한다
    private val mutex = Mutex()

    /**
     * @param failedAccessToken 401을 받은 요청에 실렸던 access token.
     * 저장된 토큰과 다르면 다른 요청이 이미 갱신한 것이므로 refresh 없이 저장된 토큰을 반환한다.
     * null이면 항상 갱신한다.
     */
    suspend fun getNewAccessToken(failedAccessToken: String? = null): String? = mutex.withLock {
        val savedAccessToken = tokenDataSource.getAccessToken()
        if (failedAccessToken != null && savedAccessToken.isNotBlank() && savedAccessToken != failedAccessToken) {
            return@withLock savedAccessToken
        }

        val result = authDataSource.refresh()
        result.getOrNull()?.let {
            tokenDataSource.saveTokens(accessToken = it.accessToken, refreshToken = it.refreshToken)
            return@withLock it.accessToken
        }

        // 성공인데 null이면 refresh token이 없는 비로그인 상태라 로그아웃하지 않는다
        val e = result.exceptionOrNull()
        when {
            e == null -> Unit
            e is HttpException && (e.code() == 401 || e.code() == 403) -> authDataSource.logout() // 서버가 거절했을 때만
            e is IOException -> Timber.w(e, "토큰 갱신 실패 (네트워크)")
            else -> Timber.e(e)
        }
        null
    }
}
