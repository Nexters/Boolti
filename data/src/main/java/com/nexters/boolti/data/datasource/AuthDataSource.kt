package com.nexters.boolti.data.datasource

import android.content.Context
import androidx.datastore.core.DataStore
import com.nexters.boolti.data.db.AppSettings
import com.nexters.boolti.data.db.dataStore
import com.nexters.boolti.data.network.api.LoginService
import com.nexters.boolti.data.network.request.RefreshRequest
import com.nexters.boolti.data.network.response.SignUpResponse
import com.nexters.boolti.data.network.response.UserResponse
import com.nexters.boolti.domain.request.LoginRequest
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import javax.inject.Inject

class AuthDataSource @Inject constructor(
    private val context: Context,
    private val loginService: LoginService,
) {
    private val dataStore: DataStore<AppSettings>
        get() = context.dataStore

    private val data: Flow<AppSettings>
        get() = dataStore.data

    val user: Flow<UserResponse?>
        get() {
            return dataStore.data.map {
                if (it.userId == null) {
                    null
                } else {
                    UserResponse(
                        id = it.userId ?: "",
                        nickname = it.nickname ?: "",
                        email = it.email ?: "",
                        imgPath = it.photo,
                    )
                }
            }
        }

    val loggedIn: Flow<Boolean>
        get() = data.map { it.accessToken.isNotEmpty() }

    suspend fun login(request: LoginRequest) = runCatching {
        loginService.kakaoLogin(request)
    }

    suspend fun logout(): Result<Unit> = runCatching {
        loginService.logout()
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

    suspend fun refresh(): Result<SignUpResponse?> = runCatching {
        val refreshToken = data.map { it.refreshToken }.first()

        if (refreshToken.isNotBlank()) loginService.refresh(RefreshRequest(refreshToken)) else null
    }

    suspend fun updateUser(user: UserResponse) {
        dataStore.updateData {
            it.copy(
                userId = user.id,
                nickname = user.nickname,
                email = user.email,
                photo = user.imgPath,
            )
        }
    }
}
