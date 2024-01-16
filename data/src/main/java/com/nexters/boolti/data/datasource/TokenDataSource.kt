package com.nexters.boolti.data.datasource

import android.content.Context
import androidx.datastore.core.DataStore
import com.nexters.boolti.data.db.AppSettings
import com.nexters.boolti.data.db.dataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.single
import kotlinx.coroutines.runBlocking
import javax.inject.Inject

class TokenDataSource @Inject constructor(
    private val context: Context,
) {
    private val dataStore: DataStore<AppSettings>
        get() = context.dataStore

    private val data: Flow<AppSettings>
        get() = dataStore.data

    fun getAccessToken(): String = runBlocking {
        data.single().accessToken
    }

    /**
     * 엑세스 토큰, 리프레시 토큰 쌍 반환
     *
     * @return first: **AccessToken**, second: **RefreshToken**
     */
    fun getTokenPair(): Pair<String, String> = runBlocking {
        data.first().run { Pair(accessToken, refreshToken) }
    }

    suspend fun saveTokens(accessToken: String, refreshToken: String) {
        dataStore.updateData {
            it.copy(
                accessToken = accessToken,
                refreshToken = refreshToken,
            )
        }
    }
}
