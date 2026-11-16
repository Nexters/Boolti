package com.nexters.boolti.presentation.screen.profileedit.nickname

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.nexters.boolti.domain.repository.AuthRepository
import com.nexters.boolti.domain.repository.UserConfigRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import timber.log.Timber
import javax.inject.Inject

@HiltViewModel
class NicknameEditViewModel @Inject constructor(
    authRepository: AuthRepository,
    private val userConfigRepository: UserConfigRepository,
) : ViewModel() {
    private var originalNickname = ""

    private val _uiState = MutableStateFlow(NicknameEditUiState())
    val uiState: StateFlow<NicknameEditUiState> = _uiState.asStateFlow()

    private val _event = Channel<NicknameEditEvent>(Channel.BUFFERED)
    val event: Flow<NicknameEditEvent> = _event.receiveAsFlow()

    init {
        viewModelScope.launch {
            authRepository.getUser()
                .onSuccess { user ->
                    originalNickname = user.nickname
                    _uiState.update { it.copy(nickname = originalNickname, loading = false) }
                }
                .onFailure {
                    Timber.e(it)
                    _event.send(NicknameEditEvent.LoadFailed)
                }
        }
    }

    fun onAction(action: NicknameEditAction) {
        when (action) {
            is NicknameEditAction.ChangeNickname -> _uiState.update { it.copy(nickname = action.nickname) }
            NicknameEditAction.Save -> save()
            NicknameEditAction.ClickBack -> if (canExit()) {
                _event.trySend(NicknameEditEvent.NavigateUp)
            } else {
                _uiState.update { it.copy(showExitAlertDialog = true) }
            }
            NicknameEditAction.DismissExitAlertDialog -> _uiState.update { it.copy(showExitAlertDialog = false) }
            NicknameEditAction.ConfirmExit -> {
                _uiState.update { it.copy(showExitAlertDialog = false) }
                _event.trySend(NicknameEditEvent.NavigateUp)
            }
        }
    }

    private fun save() {
        val state = uiState.value
        if (state.saving) return
        if (state.nickname == originalNickname) {
            _event.trySend(NicknameEditEvent.NavigateUp)
            return
        }

        _uiState.update { it.copy(saving = true) }
        viewModelScope.launch {
            userConfigRepository.saveNickname(state.nickname)
                .onSuccess { nickname ->
                    _uiState.update { it.copy(nickname = nickname, saving = false) }
                    _event.send(NicknameEditEvent.NavigateUp)
                }
                .onFailure {
                    _uiState.update { it.copy(saving = false) }
                }
        }
    }

    private fun canExit(): Boolean {
        val state = uiState.value
        return state.nickname == originalNickname || state.nicknameError != null
    }
}
