package com.nexters.boolti.presentation.screen.profile

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.viewModelScope
import com.nexters.boolti.domain.model.User
import com.nexters.boolti.domain.repository.AuthRepository
import com.nexters.boolti.domain.usecase.GetUserUsecase
import com.nexters.boolti.presentation.base.BaseViewModel
import com.nexters.boolti.presentation.screen.userId
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.filterNotNull
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.update
import javax.inject.Inject

@HiltViewModel
class ProfileViewModel @Inject constructor(
    savedStateHandle: SavedStateHandle,
    getUserUsecase: GetUserUsecase,
    authRepository: AuthRepository,
) : BaseViewModel() {
    private val id: String? = savedStateHandle[userId]

    private val _uiState = MutableStateFlow(
        ProfileState(
            user = if (id == null) (getUserUsecase() ?: User("")) else User(""),
            isMine = id == null,
        )
    )
    val uiState = _uiState.asStateFlow()

    init {
        if (uiState.value.isMine) {
            authRepository.cachedUser
                .filterNotNull()
                .onEach { user -> _uiState.update { it.copy(user = user) } }
                .launchIn(viewModelScope)
        }
    }
}
