package com.nexters.boolti.presentation.screen.accountsetting

import androidx.lifecycle.viewModelScope
import com.nexters.boolti.domain.repository.AuthRepository
import com.nexters.boolti.presentation.base.BaseViewModel
import com.nexters.boolti.presentation.extension.stateInUi
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class AccountSettingViewModel @Inject constructor(
    private val repository: AuthRepository,
) : BaseViewModel() {
    val user = repository.cachedUser
        .stateInUi(viewModelScope, null)

    val loggedIn = repository.loggedIn
        .stateInUi(viewModelScope, null)

    fun logout() {
        viewModelScope.launch {
            repository.logout()
        }
    }
}
