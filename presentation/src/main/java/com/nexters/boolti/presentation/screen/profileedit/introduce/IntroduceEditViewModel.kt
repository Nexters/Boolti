package com.nexters.boolti.presentation.screen.profileedit.introduce

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
class IntroduceEditViewModel @Inject constructor(
    getCachedUserUseCase: GetCachedUserUseCase,
    private val userConfigRepository: UserConfigRepository,
) : ViewModel() {
    private val originalIntroduce = getCachedUserUseCase()?.introduction.orEmpty()

    private val _uiState = MutableStateFlow(IntroduceEditUiState(introduce = originalIntroduce))
    val uiState: StateFlow<IntroduceEditUiState> = _uiState.asStateFlow()

    private val _event = Channel<IntroduceEditEvent>(Channel.BUFFERED)
    val event: Flow<IntroduceEditEvent> = _event.receiveAsFlow()

    fun onAction(action: IntroduceEditAction) {
        when (action) {
            is IntroduceEditAction.ChangeIntroduce -> _uiState.update { it.copy(introduce = action.introduce) }
            IntroduceEditAction.Save -> save()
            IntroduceEditAction.ClickBack -> if (canExit()) {
                _event.trySend(IntroduceEditEvent.NavigateUp)
            } else {
                _uiState.update { it.copy(showExitAlertDialog = true) }
            }
            IntroduceEditAction.DismissExitAlertDialog -> _uiState.update { it.copy(showExitAlertDialog = false) }
            IntroduceEditAction.ConfirmExit -> {
                _uiState.update { it.copy(showExitAlertDialog = false) }
                _event.trySend(IntroduceEditEvent.NavigateUp)
            }
        }
    }

    private fun save() {
        val state = uiState.value
        if (state.saving) return
        if (state.introduce == originalIntroduce) {
            _event.trySend(IntroduceEditEvent.NavigateUp)
            return
        }

        _uiState.update { it.copy(saving = true) }
        viewModelScope.launch {
            userConfigRepository.saveIntroduce(state.introduce)
                .onSuccess { introduce ->
                    _uiState.update { it.copy(introduce = introduce, saving = false) }
                    _event.send(IntroduceEditEvent.NavigateUp)
                }
                .onFailure {
                    _uiState.update { it.copy(saving = false) }
                }
        }
    }

    private fun canExit(): Boolean = uiState.value.introduce == originalIntroduce
}
