package com.nexters.boolti.data.network

import com.nexters.boolti.data.datasource.TokenDataSource
import kotlinx.coroutines.runBlocking
import okhttp3.Interceptor
import okhttp3.Response
import javax.inject.Inject

internal class AuthInterceptor @Inject constructor(
    private val tokenDataSource: TokenDataSource,
) : Interceptor {
    override fun intercept(chain: Interceptor.Chain): Response {
        val accessToken = runBlocking { tokenDataSource.getAccessToken() }

        return chain.proceed(
            chain.request().newBuilder()
                .addHeader("Authorization", "Bearer $accessToken")
                .build(),
        )
    }
}
