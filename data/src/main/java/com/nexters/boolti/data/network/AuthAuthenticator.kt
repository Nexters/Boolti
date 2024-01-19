package com.nexters.boolti.data.network

import com.nexters.boolti.data.datasource.TokenDataSource
import okhttp3.Authenticator
import okhttp3.Request
import okhttp3.Response
import okhttp3.Route
import javax.inject.Inject

class AuthAuthenticator @Inject constructor(
    private val tokenDataSource: TokenDataSource,
    private val authDataSource: AuthDataSource,
    private val apiService: ApiService,
) : Authenticator {
    override fun authenticate(route: Route?, response: Response): Request? {
        return null
    }
}
