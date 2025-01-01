package com.nexters.boolti.data.datasource

import javax.inject.Inject

internal class AuthTokenDataSource @Inject constructor(
    private val authDataSource: AuthDataSource,
    private val tokenDataSource: TokenDataSource,
) {
    suspend fun getNewAccessToken(): String? {
        val response = authDataSource.refresh()
        val newToken = response.getOrNull()
        return newToken?.let {
            tokenDataSource.saveTokens(
                accessToken = it.accessToken,
                refreshToken = it.refreshToken,
            )
            it.accessToken
        } ?: run {
            authDataSource.logout()
            null
        }
    }
}
