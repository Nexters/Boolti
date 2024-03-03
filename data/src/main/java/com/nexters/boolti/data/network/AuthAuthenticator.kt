package com.nexters.boolti.data.network

import com.nexters.boolti.data.datasource.AuthDataSource
import com.nexters.boolti.data.datasource.TokenDataSource
import kotlinx.coroutines.runBlocking
import okhttp3.Authenticator
import okhttp3.Request
import okhttp3.Response
import okhttp3.Route
import javax.inject.Inject

internal class AuthAuthenticator @Inject constructor(
    private val tokenDataSource: TokenDataSource,
    private val authDataSource: AuthDataSource,
) : Authenticator {
    override fun authenticate(route: Route?, response: Response): Request? {
        val accessToken = runBlocking { getNewAccessToken() } ?: return null

        return response.request.newBuilder().header("Authorization", "Bearer $accessToken").build()
    }

    private suspend fun getNewAccessToken(): String? {
        val response = authDataSource.refresh()
        val newToken = response.getOrNull()
        return newToken?.let {
            tokenDataSource.saveTokens(
                accessToken = it.accessToken,
                refreshToken = it.refreshToken,
            )
            it.accessToken
        }
    }
}
