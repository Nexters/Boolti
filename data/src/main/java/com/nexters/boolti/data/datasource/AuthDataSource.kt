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
import com.nexters.boolti.domain.model.PreviewList
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
                        link = PreviewList(
                            totalSize = it.profileLinkCount,
                            hasMoreItems = it.hasMoreLink,
                            previewItems = it.profileLink,
                        ),
                        comingSoonShow = PreviewList(
                            totalSize = it.upcomingShowCount,
                            hasMoreItems = it.hasMoreUpcomingShow,
                            previewItems = it.upcomingShow,
                            isVisible = it.showUpcomingShow,
                        ),
                        performedShow = PreviewList(
                            totalSize = it.performedShowCount,
                            hasMoreItems = it.hasMorePerformedShow,
                            previewItems = it.performedShow,
                            isVisible = it.showPerformedShow,
                        ),
                        video = PreviewList(
                            totalSize = it.videoCount,
                            hasMoreItems = it.hasMoreVideoItem,
                            previewItems = it.video,
                        ),
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
                hasMoreLink = false,

                performedShow = emptyList(),
                performedShowCount = 0,
                hasMorePerformedShow = false,
                showPerformedShow = false,

                upcomingShow = emptyList(),
                upcomingShowCount = 0,
                hasMoreUpcomingShow = false,
                showUpcomingShow = false,

                videoCount = 0,
                hasMoreVideoItem = false,
                video = emptyList(),

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

                profileLink = user.link.previewItems,
                profileLinkCount = user.link.totalSize,
                hasMoreLink = user.link.hasMoreItems,

                performedShow = user.performedShow.previewItems,
                performedShowCount = user.performedShow.totalSize,
                hasMorePerformedShow = user.performedShow.hasMoreItems,
                showPerformedShow = user.performedShow.isVisible == true,

                upcomingShow = user.comingSoonShow.previewItems,
                upcomingShowCount = user.comingSoonShow.totalSize,
                hasMoreUpcomingShow = user.comingSoonShow.hasMoreItems,
                showUpcomingShow = user.comingSoonShow.isVisible == true,

                videoCount = user.video.totalSize,
                hasMoreVideoItem = user.video.hasMoreItems,
                video = user.video.previewItems,
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
