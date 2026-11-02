package com.nexters.boolti.presentation.screen.profileedit.sns

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.nexters.boolti.domain.model.Sns
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
class SnsEditViewModel @Inject constructor(
    getCachedUserUseCase: GetCachedUserUseCase,
    private val userConfigRepository: UserConfigRepository,
) : ViewModel() {
    private val snsList: Map<Sns.SnsType, String>? =
        getCachedUserUseCase()?.sns?.associate { it.type to it.username }
    private val originalInstagramUsername: String = snsList?.get(Sns.SnsType.INSTAGRAM).orEmpty()
    private val originalYoutubeUsername: String = snsList?.get(Sns.SnsType.YOUTUBE).orEmpty()

    private val _uiState = MutableStateFlow(
        SnsEditUiState(
            originalInstagramUsername = originalInstagramUsername,
            originalYoutubeUsername = originalYoutubeUsername,
            instagramUsername = originalInstagramUsername,
            youtubeUsername = originalYoutubeUsername,
        )
    )
    val uiState: StateFlow<SnsEditUiState> = _uiState.asStateFlow()

    private val _event = Channel<SnsEditEvent>(Channel.BUFFERED)
    val event: Flow<SnsEditEvent> = _event.receiveAsFlow()

    fun onAction(action: SnsEditAction) {
        when (action) {
            is SnsEditAction.ChangeInstagramUsername -> _uiState.update { it.copy(instagramUsername = action.username) }
            is SnsEditAction.ChangeYoutubeUsername -> _uiState.update { it.copy(youtubeUsername = action.username) }
            SnsEditAction.Save -> save()
            SnsEditAction.ClickBack -> if (canExit()) {
                _event.trySend(SnsEditEvent.NavigateUp)
            } else {
                _uiState.update { it.copy(showExitAlertDialog = true) }
            }
            SnsEditAction.DismissExitAlertDialog -> _uiState.update { it.copy(showExitAlertDialog = false) }
            SnsEditAction.ConfirmExit -> {
                _uiState.update { it.copy(showExitAlertDialog = false) }
                _event.trySend(SnsEditEvent.NavigateUp)
            }
        }
    }

    private fun save() {
        val state = uiState.value
        if (state.saving) return

        _uiState.update { it.copy(saving = true) }
        viewModelScope.launch {
            userConfigRepository.saveSns(
                listOf(
                    Sns(id = "", type = Sns.SnsType.INSTAGRAM, username = state.instagramUsername),
                    Sns(id = "", type = Sns.SnsType.YOUTUBE, username = state.youtubeUsername),
                )
            )
                .onSuccess {
                    _uiState.update { it.copy(saving = false) }
                    _event.send(SnsEditEvent.NavigateUp)
                }
                .onFailure {
                    _uiState.update { it.copy(saving = false) }
                }
        }
    }

    private fun canExit(): Boolean {
        val state = uiState.value
        val unchanged = state.instagramUsername == originalInstagramUsername &&
                state.youtubeUsername == originalYoutubeUsername
        return unchanged || state.instagramUsernameError != null || state.youtubeUsernameError != null
    }
}
