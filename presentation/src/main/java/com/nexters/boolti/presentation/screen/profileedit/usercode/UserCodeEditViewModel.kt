package com.nexters.boolti.presentation.screen.profileedit.usercode

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.nexters.boolti.domain.repository.UserConfigRepository
import com.nexters.boolti.domain.usecase.GetCachedUserUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Job
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject
import kotlin.time.Duration.Companion.milliseconds

@HiltViewModel
class UserCodeEditViewModel @Inject constructor(
    getCachedUserUseCase: GetCachedUserUseCase,
    private val userConfigRepository: UserConfigRepository,
) : ViewModel() {
    private val originalUserCode = getCachedUserUseCase()?.userCode.orEmpty().lowercase()

    private val _uiState = MutableStateFlow(UserCodeEditUiState(userCode = originalUserCode))
    val uiState: StateFlow<UserCodeEditUiState> = _uiState.asStateFlow()

    private val _event = Channel<UserCodeEditEvent>(Channel.BUFFERED)
    val event: Flow<UserCodeEditEvent> = _event.receiveAsFlow()

    private var debounceJob: Job? = null

    fun onAction(action: UserCodeEditAction) {
        when (action) {
            is UserCodeEditAction.ChangeUserCode -> changeUserCode(action.userCode)
            UserCodeEditAction.Save -> save()
            UserCodeEditAction.ClickBack -> if (canExit()) {
                _event.trySend(UserCodeEditEvent.NavigateUp)
            } else {
                _uiState.update { it.copy(showExitAlertDialog = true) }
            }
            UserCodeEditAction.DismissExitAlertDialog -> _uiState.update { it.copy(showExitAlertDialog = false) }
            UserCodeEditAction.ConfirmExit -> {
                _uiState.update { it.copy(showExitAlertDialog = false) }
                _event.trySend(UserCodeEditEvent.NavigateUp)
            }
        }
    }

    private fun changeUserCode(userCode: String) {
        val newUserCode = userCode.lowercase()
        _uiState.update { it.copy(userCode = newUserCode) }

        debounceJob?.cancel()

        if (newUserCode != originalUserCode && uiState.value.userCodeError == null) {
            debounceJob = viewModelScope.launch {
                delay(500.milliseconds)
                checkUserCodeDuplication(newUserCode)
            }
        } else {
            // 원래 코드로 돌아왔거나 형식이 틀리면 중복 확인 중 상태를 푼다
            _uiState.update { it.copy(checkingDuplicated = false) }
        }
    }

    private fun save() {
        val state = uiState.value
        if (state.saving) return
        if (state.userCode == originalUserCode) {
            _event.trySend(UserCodeEditEvent.NavigateUp)
            return
        }

        _uiState.update { it.copy(saving = true) }
        viewModelScope.launch {
            userConfigRepository.saveUserCode(state.userCode)
                .onSuccess { userCode ->
                    _uiState.update { it.copy(userCode = userCode, saving = false) }
                    _event.send(UserCodeEditEvent.NavigateUp)
                }
                .onFailure {
                    _uiState.update { it.copy(saving = false) }
                }
        }
    }

    private fun canExit(): Boolean {
        val state = uiState.value
        return state.userCode == originalUserCode || state.userCodeError != null
    }

    private suspend fun checkUserCodeDuplication(userCode: String) {
        _uiState.update { it.copy(checkingDuplicated = true) }

        userConfigRepository.checkUserCodeDuplicated(userCode)
            .onSuccess { isDuplicated ->
                _uiState.update { state ->
                    val duplicatedUserCodes = if (isDuplicated) {
                        (state.duplicatedUserCodes + userCode).distinct()
                    } else {
                        state.duplicatedUserCodes - userCode
                    }
                    state.copy(checkingDuplicated = false, duplicatedUserCodes = duplicatedUserCodes)
                }
            }
            .onFailure {
                _uiState.update { it.copy(checkingDuplicated = false) }
            }
    }
}
