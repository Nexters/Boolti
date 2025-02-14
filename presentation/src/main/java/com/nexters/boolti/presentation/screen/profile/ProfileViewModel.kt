package com.nexters.boolti.presentation.screen.profile

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.viewModelScope
import androidx.navigation.toRoute
import com.nexters.boolti.domain.model.User
import com.nexters.boolti.domain.repository.AuthRepository
import com.nexters.boolti.domain.repository.MemberRepository
import com.nexters.boolti.domain.usecase.GetUserUsecase
import com.nexters.boolti.presentation.base.BaseViewModel
import com.nexters.boolti.presentation.screen.navigation.MainRoute
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.filterNotNull
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ProfileViewModel @Inject constructor(
    savedStateHandle: SavedStateHandle,
    getUserUsecase: GetUserUsecase,
    private val memberRepository: MemberRepository,
    private val authRepository: AuthRepository,
) : BaseViewModel() {
    private val _userCode: String? = savedStateHandle.toRoute<MainRoute.Profile>().userCode
    private val myProfile: User.My = getUserUsecase() ?: User.My("-999")
    private val isMyProfile = _userCode?.equals(myProfile.userCode) ?: true

    private val _uiState = MutableStateFlow(
        ProfileState(
            isMine = isMyProfile,
            user = if (isMyProfile) myProfile else User.Others(""),
        )
    )
    val uiState = _uiState.asStateFlow()

    private val _event = Channel<ProfileEvent>()
    val event = _event.receiveAsFlow()

    init {
        if (isMyProfile) {
            collectMyProfile()
        } else {
            fetchOthersProfile(_userCode!!)
        }
    }

    private fun event(event: ProfileEvent) {
        viewModelScope.launch {
            _event.send(event)
        }
    }

    private fun collectMyProfile() {
        authRepository.cachedUser
            .filterNotNull()
            .onEach { user ->
                _uiState.update { it.copy(user = user) }
            }
            .launchIn(viewModelScope)
    }

    private fun fetchOthersProfile(userCode: String) {
        viewModelScope.launch(recordExceptionHandler) {
            memberRepository.getMember(userCode).onSuccess { user ->
                _uiState.update { it.copy(user = user) }
            }.onFailure {
                event(ProfileEvent.Invalid)
            }
        }
    }
}
