package com.nexters.boolti.presentation.screen.showregistration

import androidx.lifecycle.ViewModel
import com.nexters.boolti.domain.repository.AuthRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.first
import javax.inject.Inject

@HiltViewModel
class ShowRegistrationViewModel @Inject constructor(
    val authRepository: AuthRepository,
) : ViewModel() {
    suspend fun refreshAndGetToken() = authRepository.refreshToken().first()
}
