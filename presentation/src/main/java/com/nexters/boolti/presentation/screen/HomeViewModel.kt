package com.nexters.boolti.presentation.screen

import androidx.lifecycle.viewModelScope
import com.nexters.boolti.domain.repository.AuthRepository
import com.nexters.boolti.presentation.base.BaseViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import kotlinx.coroutines.plus
import javax.inject.Inject

@HiltViewModel
class HomeViewModel @Inject constructor(
    private val authRepository: AuthRepository,
) : BaseViewModel() {
    val loggedIn = authRepository.loggedIn.stateIn(
        viewModelScope,
        SharingStarted.WhileSubscribed(5000),
        null,
    )

    init {
        initUserInfo()
        sendFcmToken()
    }

    private fun initUserInfo() {
        authRepository.getUserAndCache()
            .launchIn(viewModelScope + recordExceptionHandler)
    }

    private fun sendFcmToken() {
        viewModelScope.launch {
            loggedIn.collectLatest {
                if (it == true) authRepository.sendFcmToken()
            }
        }
    }
}
