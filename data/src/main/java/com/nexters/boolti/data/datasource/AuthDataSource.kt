package com.nexters.boolti.data.datasource

import android.content.Context
import androidx.datastore.core.DataStore
import com.google.firebase.analytics.FirebaseAnalytics
import com.google.firebase.analytics.ktx.analytics
import com.google.firebase.ktx.Firebase
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

internal class AuthDataSource @Inject constructor(
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
                        id = it.userId,
                        nickname = it.nickname ?: "",
                        email = it.email ?: "",
                        imgPath = it.photo,
                        userCode = it.userCode,
                        introduction = it.profileIntroduction,
                        sns = it.profileSns,
                        link = it.profileLink,
                        performedShow = it.performedShow,
                    )
                }
            }
        }

    val loggedIn: Flow<Boolean>
        get() = data.map { it.accessToken.isNotBlank() }

    suspend fun login(request: LoginRequest) = runCatching {
        loginService.kakaoLogin(request)
    }.onSuccess {
        Firebase.analytics.logEvent(FirebaseAnalytics.Event.LOGIN, null)
    }

    suspend fun logout(): Result<Unit> = runCatching {
        localLogout()
        loginService.logout()
    }

    suspend fun localLogout() {
        dataStore.updateData {
            it.copy(
                userId = null,
                loginType = null,
                nickname = null,
                email = null,
                phoneNumber = null,
                photo = null,
                userCode = null,
                profileIntroduction = "",
                profileSns = emptyList(),
                profileLink = emptyList(),
                accessToken = "",
                refreshToken = "",
            )
        }
        Firebase.analytics.setUserId(null)
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
                userCode = user.userCode,
                profileIntroduction = user.introduction,
                profileSns = user.sns,
                profileLink = user.link,
            )
        }
        Firebase.analytics.apply {
            setUserId(user.id)
            setUserProperty("nickname", user.nickname)
        }
    }

    /**
     * @return [accessToken, refreshToken]
     */
    fun getTokens(): Flow<Pair<String, String>> = data.map { it.accessToken to it.refreshToken }
}
