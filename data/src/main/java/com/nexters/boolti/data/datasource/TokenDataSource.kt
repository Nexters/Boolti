package com.nexters.boolti.data.datasource

import android.content.Context
import androidx.datastore.core.DataStore
import com.google.android.gms.tasks.OnCompleteListener
import com.google.firebase.messaging.FirebaseMessaging
import com.nexters.boolti.data.db.AppSettings
import com.nexters.boolti.data.db.dataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import javax.inject.Inject
import kotlin.coroutines.resume
import kotlin.coroutines.suspendCoroutine

class TokenDataSource @Inject constructor(
    private val context: Context,
) {
    private val dataStore: DataStore<AppSettings>
        get() = context.dataStore

    private val data: Flow<AppSettings>
        get() = dataStore.data

    suspend fun getAccessToken(): String = data.first().accessToken

    /**
     * 엑세스 토큰, 리프레시 토큰 쌍 반환
     *
     * @return first: **AccessToken**, second: **RefreshToken**
     */
    suspend fun getTokenPair(): Pair<String, String> =
        data.first().run { Pair(accessToken, refreshToken) }

    suspend fun saveTokens(accessToken: String, refreshToken: String) {
        dataStore.updateData {
            it.copy(
                accessToken = accessToken,
                refreshToken = refreshToken,
            )
        }
    }

    suspend fun getFcmToken(): String? = suspendCoroutine { continuation ->
        val firebaseMessaging = FirebaseMessaging.getInstance()
        firebaseMessaging.token.addOnCompleteListener(OnCompleteListener { task ->
            if (!task.isSuccessful) {
                continuation.resume(null)
                return@OnCompleteListener
            }

            val token = task.result

            continuation.resume(token)
        })
    }
}
