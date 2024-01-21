package com.nexters.boolti.presentation.screen.my

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.nexters.boolti.domain.repository.AuthRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class MyViewModel @Inject constructor(
    private val authRepository: AuthRepository,
) : ViewModel() {
    fun logout() {
        viewModelScope.launch {
            authRepository.logout()
        }
    }
}
