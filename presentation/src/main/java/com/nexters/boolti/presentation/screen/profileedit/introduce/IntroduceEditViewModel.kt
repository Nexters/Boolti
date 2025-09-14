package com.nexters.boolti.presentation.screen.profileedit.introduce

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
class IntroduceEditViewModel @Inject constructor(
    getUserUseCase: GetUserUsecase,
    private val userConfigRepository: UserConfigRepository,
) : ViewModel() {
    private val originalIntroduce = getUserUseCase()?.introduction.orEmpty()

    private val _uiState = MutableStateFlow(IntroduceEditState(introduce = originalIntroduce))
    val uiState = _uiState.asStateFlow()

    private val _event = Channel<IntroduceEditEvent>()
    val event = _event.receiveAsFlow()

    val maxLength = 60

    fun changeIntroduction(introduction: String) {
        _uiState.update { it.copy(introduce = introduction) }
    }

    fun saveIntroduction() {
        if (uiState.value.saving) return
        if (uiState.value.introduce == originalIntroduce) {
            event(IntroduceEditEvent.Saved)
            return
        }

        _uiState.update { it.copy(saving = true) }
        viewModelScope.launch {
            userConfigRepository.saveIntroduce(uiState.value.introduce)
                .onSuccess { introduction ->
                    _uiState.update { it.copy(introduce = introduction, saving = false) }
                    event(IntroduceEditEvent.Saved)
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
        val canExit = uiState.value.introduce == originalIntroduce
        if (!canExit) showExitAlertDialog()
        return canExit
    }

    private fun event(event: IntroduceEditEvent) {
        viewModelScope.launch {
            _event.send(event)
        }
    }
}
