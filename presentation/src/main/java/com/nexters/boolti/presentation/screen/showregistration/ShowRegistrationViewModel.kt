package com.nexters.boolti.presentation.screen.showregistration

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.nexters.boolti.domain.repository.AuthRepository
import com.nexters.boolti.presentation.extension.stateInUi
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class ShowRegistrationViewModel @Inject constructor(
    val authRepository: AuthRepository,
) : ViewModel() {
    val tokens = authRepository.getTokens()
        .stateInUi(viewModelScope, null)
}