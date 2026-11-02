package com.nexters.boolti.data.network

import com.nexters.boolti.data.datasource.AuthTokenDataSource
import kotlinx.coroutines.runBlocking
import okhttp3.Authenticator
import okhttp3.Request
import okhttp3.Response
import okhttp3.Route
import javax.inject.Inject

internal class AuthAuthenticator @Inject constructor(
    private val authTokenDataSource: AuthTokenDataSource,
) : Authenticator {
    override fun authenticate(route: Route?, response: Response): Request? {
        // 갱신한 토큰으로 이미 한 번 재시도했는데 또 401이면 멈춘다
        if (response.priorResponse != null) return null

        val failedAccessToken = response.request.header("Authorization")?.removePrefix("Bearer ")
        val accessToken = runBlocking { authTokenDataSource.getNewAccessToken(failedAccessToken) } ?: return null

        return response.request.newBuilder().header("Authorization", "Bearer $accessToken").build()
    }
}
