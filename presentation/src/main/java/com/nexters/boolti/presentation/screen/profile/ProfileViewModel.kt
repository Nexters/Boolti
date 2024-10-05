package com.nexters.boolti.presentation.screen.profile

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.viewModelScope
import com.nexters.boolti.domain.model.User
import com.nexters.boolti.domain.repository.AuthRepository
import com.nexters.boolti.domain.repository.MemberRepository
import com.nexters.boolti.domain.usecase.GetUserUsecase
import com.nexters.boolti.presentation.base.BaseViewModel
import com.nexters.boolti.presentation.screen.userCode
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.filterNotNull
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ProfileViewModel @Inject constructor(
    savedStateHandle: SavedStateHandle,
    getUserUsecase: GetUserUsecase,
    memberRepository: MemberRepository,
    authRepository: AuthRepository,
) : BaseViewModel() {
    private val _userCode: String? = savedStateHandle[userCode]
    private val myProfile: User.My? = getUserUsecase()

    private val _uiState = MutableStateFlow(
        ProfileState(
            isMine = _userCode == null || myProfile?.userCode == _userCode,
            user = if (_userCode == null) myProfile ?: User.My("") else User.My(""),
        )
    )
    val uiState = _uiState.asStateFlow()

    init {
        _userCode?.let { userCode ->
            viewModelScope.launch {
                memberRepository.getMember(userCode).onSuccess { user ->
                    _uiState.update { it.copy(user = user) }
                }
            }
        } ?: authRepository.cachedUser
            .filterNotNull()
            .onEach { user -> _uiState.update { it.copy(user = user) } }
            .launchIn(viewModelScope)
    }
}
