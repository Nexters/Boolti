package com.nexters.boolti.presentation.screen.profileedit.nickname

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.nexters.boolti.domain.repository.UserConfigRepository
import com.nexters.boolti.domain.usecase.GetUserUsecase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class NicknameEditViewModel @Inject constructor(
    getUserUseCase: GetUserUsecase,
    private val userConfigRepository: UserConfigRepository,
) : ViewModel() {
    private val originalNickname = getUserUseCase()?.nickname.orEmpty()

    private val _uiState = MutableStateFlow(NicknameEditState(nickname = originalNickname))
    val uiState = _uiState.asStateFlow()

    private val _event = Channel<NicknameEditEvent>()
    val event = _event.receiveAsFlow()

    fun changeNickname(nickname: String) {
        _uiState.update { it.copy(nickname = nickname) }
    }

    fun saveNickname() {
        if (uiState.value.saving) return
        if (uiState.value.nickname == originalNickname) {
            event(NicknameEditEvent.Saved)
            return
        }

        _uiState.update { it.copy(saving = true) }
        viewModelScope.launch {
            userConfigRepository.saveNickname(uiState.value.nickname)
                .onSuccess { nickname ->
                    _uiState.update { it.copy(nickname = nickname, saving = false) }
                    event(NicknameEditEvent.Saved)
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
        val canExit = uiState.value.nickname == originalNickname ||
                uiState.value.nicknameError != null
        if (!canExit) showExitAlertDialog()
        return canExit
    }

    private fun event(event: NicknameEditEvent) {
        viewModelScope.launch {
            _event.send(event)
        }
    }
}
