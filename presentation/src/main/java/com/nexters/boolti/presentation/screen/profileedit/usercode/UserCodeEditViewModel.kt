package com.nexters.boolti.presentation.screen.profileedit.usercode

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.nexters.boolti.domain.repository.UserConfigRepository
import com.nexters.boolti.domain.usecase.GetUserUsecase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Job
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class UserCodeEditViewModel @Inject constructor(
    getUserUseCase: GetUserUsecase,
    private val userConfigRepository: UserConfigRepository,
) : ViewModel() {
    private val originalUserCode = getUserUseCase()?.userCode.orEmpty().lowercase()

    private val _uiState = MutableStateFlow(UserCodeEditState(userCode = originalUserCode))
    val uiState = _uiState.asStateFlow()

    private val _event = Channel<UserCodeEditEvent>()
    val event = _event.receiveAsFlow()

    private var debounceJob: Job? = null

    fun changeUserCode(userCode: String) {
        val newUserCode = userCode.lowercase()
        _uiState.update { it.copy(userCode = newUserCode) }

        debounceJob?.cancel()

        if (newUserCode != originalUserCode && uiState.value.userCodeError == null) {
            debounceJob = viewModelScope.launch {
                delay(500) // Wait 500ms
                checkUserCodeDuplication(newUserCode)
            }
        } else {
            // Clear checking state if user code is same as original or invalid
            _uiState.update { it.copy(checkingDuplicated = false) }
        }
    }

    fun saveUserCode() {
        if (uiState.value.saving) return
        if (uiState.value.userCode == originalUserCode) {
            event(UserCodeEditEvent.Saved)
            return
        }

        _uiState.update { it.copy(saving = true) }
        viewModelScope.launch {
            userConfigRepository.saveUserCode(uiState.value.userCode)
                .onSuccess { userCode ->
                    _uiState.update { it.copy(userCode = userCode, saving = false) }
                    event(UserCodeEditEvent.Saved)
                }
                .onFailure {
                    _uiState.update { it.copy(saving = false) }
                }
        }
    }

    fun showExitAlertDialog() {
        _uiState.update { it.copy(showExitAlertDialog = true) }
    }

    fun dismissExitAlertDialog() {
        _uiState.update { it.copy(showExitAlertDialog = false) }
    }

    fun checkCanExit(): Boolean {
        val canExit = uiState.value.userCode == originalUserCode ||
                uiState.value.userCodeError != null
        if (!canExit) showExitAlertDialog()
        return canExit
    }

    private suspend fun checkUserCodeDuplication(userCode: String) {
        _uiState.update { it.copy(checkingDuplicated = true) }

        try {
            val result = userConfigRepository.checkUserCodeDuplicated(userCode)
            result.onSuccess { isDuplicated ->
                _uiState.update { currentState ->
                    val updatedDuplicatedCodes = if (isDuplicated) {
                        if (!currentState.duplicatedUserCodes.contains(userCode)) {
                            currentState.duplicatedUserCodes + userCode
                        } else {
                            currentState.duplicatedUserCodes
                        }
                    } else {
                        currentState.duplicatedUserCodes.filterNot { it == userCode }
                    }

                    currentState.copy(
                        checkingDuplicated = false,
                        duplicatedUserCodes = updatedDuplicatedCodes,
                    )
                }
            }.onFailure {
                _uiState.update { it.copy(checkingDuplicated = false) }
            }
        } catch (_: Exception) {
            _uiState.update { it.copy(checkingDuplicated = false) }
        }
    }

    private fun event(event: UserCodeEditEvent) {
        viewModelScope.launch {
            _event.send(event)
        }
    }
}
