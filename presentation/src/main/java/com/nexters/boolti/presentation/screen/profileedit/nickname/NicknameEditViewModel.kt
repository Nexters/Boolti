package com.nexters.boolti.presentation.screen.profileedit.nickname

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.nexters.boolti.domain.repository.UserConfigRepository
import com.nexters.boolti.domain.usecase.GetCachedUserUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class NicknameEditViewModel @Inject constructor(
    getCachedUserUseCase: GetCachedUserUseCase,
    private val userConfigRepository: UserConfigRepository,
) : ViewModel() {
    private val originalNickname = getCachedUserUseCase()?.nickname.orEmpty()

    private val _uiState = MutableStateFlow(NicknameEditUiState(nickname = originalNickname))
    val uiState: StateFlow<NicknameEditUiState> = _uiState.asStateFlow()

    private val _event = Channel<NicknameEditEvent>(Channel.BUFFERED)
    val event: Flow<NicknameEditEvent> = _event.receiveAsFlow()

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
