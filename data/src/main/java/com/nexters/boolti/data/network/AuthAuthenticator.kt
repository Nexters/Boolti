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
        val accessToken = runBlocking { authTokenDataSource.getNewAccessToken() } ?: return null

        return response.request.newBuilder().header("Authorization", "Bearer $accessToken").build()
    }
}
