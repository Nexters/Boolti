package com.nexters.boolti.presentation.screen.login

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.crashlytics.FirebaseCrashlytics
import com.nexters.boolti.domain.repository.AuthRepository
import com.nexters.boolti.domain.request.LoginRequest
import com.nexters.boolti.domain.request.OauthType
import com.nexters.boolti.domain.request.SignUpRequest
import com.nexters.boolti.presentation.util.JwtUtil
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import timber.log.Timber
import javax.inject.Inject

@HiltViewModel
class LoginViewModel @Inject constructor(
    private val authRepository: AuthRepository,
) : ViewModel() {
    private val _uiState = MutableStateFlow(LoginUiState())
    val uiState: StateFlow<LoginUiState> = _uiState.asStateFlow()

    private val _event = Channel<LoginEvent>()
    val event = _event.receiveAsFlow()

    fun loginWithKaKao(accessToken: String, idToken: String) {
        updateUserInfoFromKaKao(idToken = idToken)

        viewModelScope.launch {
            authRepository.kakaoLogin(LoginRequest(accessToken)).onSuccess {
                when {
                    it.signUpRequired -> event(LoginEvent.RequireSignUp)
                    it.signOutCancelled -> event(LoginEvent.SignOutCancelled)
                    else -> event(LoginEvent.Success)
                }
            }.onFailure {
                FirebaseCrashlytics.getInstance().setCustomKey("LOGIN", "FAILED")
                event(LoginEvent.Invalid)
            }
        }
    }

    private fun updateUserInfoFromKaKao(idToken: String) {
        val payloadMap = JwtUtil().decodePayload(idToken)

        val profileImageUrl = payloadMap["picture"]?.replace("http:", "https:")
        val nickname = payloadMap["nickname"]
        val userId = payloadMap["sub"]!! // todo : sub가 null일 때 처리하기. 근데 그러면 안 되는데...
        val email = payloadMap["email"]

        _uiState.update {
            it.copy(
                email = email,
                nickname = nickname,
                profileImageUrl = profileImageUrl,
                userId = userId,
            )
        }
    }

    fun signUp() {
        viewModelScope.launch {
            val loginState = uiState.value
            authRepository.signUp(
                SignUpRequest(
                    email = loginState.email,
                    nickname = loginState.nickname,
                    imgPath = loginState.profileImageUrl,
                    phoneNumber = null,
                    oauthType = OauthType.KAKAO,
                    oauthIdentity = loginState.userId,
                )
            ).onSuccess {
                event(LoginEvent.Success)
            }.onFailure {
                FirebaseCrashlytics.getInstance().setCustomKey("SIGNUP", "FAILED")
                event(LoginEvent.SignupFailed)
            }
        }
    }

    private fun event(event: LoginEvent) {
        viewModelScope.launch {
            _event.send(event)
        }
    }
}
