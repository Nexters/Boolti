package com.nexters.boolti.presentation.screen.login

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.nexters.boolti.domain.repository.AuthRepository
import com.nexters.boolti.domain.repository.SignUpRepository
import com.nexters.boolti.domain.request.LoginRequest
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.launch
import timber.log.Timber
import javax.inject.Inject

@HiltViewModel
class LoginViewModel @Inject constructor(
    private val authRepository: AuthRepository,
    private val signUpRepository: SignUpRepository,
) : ViewModel() {
    private val _event = Channel<LoginEvent>()
    val event = _event.receiveAsFlow()

    fun login(accessToken: String) {
        viewModelScope.launch {
            authRepository.kakaoLogin(LoginRequest(accessToken)).onSuccess {
                if (it) event(LoginEvent.Success) else event(LoginEvent.RequireSignUp)
            }.onFailure {
                Timber.d("login failed: $it")
                event(LoginEvent.Invalid)
            }
        }
    }

    private fun event(event: LoginEvent) {
        viewModelScope.launch {
            _event.send(event)
        }
    }
}
