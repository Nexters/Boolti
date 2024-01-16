package com.nexters.boolti.data.network

import android.content.Context
import androidx.datastore.core.DataStore
import com.nexters.boolti.data.db.AppSettings
import com.nexters.boolti.data.db.dataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject

class AuthDataSource @Inject constructor(
    private val context: Context,
    private val apiService: ApiService,
) {
    private val dataStore: DataStore<AppSettings>
        get() = context.dataStore

    private val data: Flow<AppSettings>
        get() = dataStore.data

    val loggedIn: Flow<Boolean>
        get() = data.map { it.userId != null }

    suspend fun login() {}
    suspend fun logout() {
        dataStore.updateData {
            it.copy(
                userId = null,
                loginType = null,
                nickname = null,
                email = null,
                phoneNumber = null,
                accessToken = "",
                refreshToken = ""
            )
        }
    }
}
