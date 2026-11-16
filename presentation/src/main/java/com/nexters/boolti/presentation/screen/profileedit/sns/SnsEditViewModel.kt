package com.nexters.boolti.presentation.screen.profileedit.sns

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.nexters.boolti.domain.model.Sns
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
class SnsEditViewModel @Inject constructor(
    authRepository: AuthRepository,
    private val userConfigRepository: UserConfigRepository,
) : ViewModel() {
    private val _uiState = MutableStateFlow(SnsEditUiState())
    val uiState: StateFlow<SnsEditUiState> = _uiState.asStateFlow()

    private val _event = Channel<SnsEditEvent>(Channel.BUFFERED)
    val event: Flow<SnsEditEvent> = _event.receiveAsFlow()

    init {
        viewModelScope.launch {
            authRepository.getUser()
                .onSuccess { user ->
                    val snsList = user.sns.associate { it.type to it.username }
                    val instagram = snsList[Sns.SnsType.INSTAGRAM].orEmpty()
                    val youtube = snsList[Sns.SnsType.YOUTUBE].orEmpty()
                    _uiState.update {
                        it.copy(
                            originalInstagramUsername = instagram,
                            originalYoutubeUsername = youtube,
                            instagramUsername = instagram,
                            youtubeUsername = youtube,
                            loading = false,
                        )
                    }
                }
                .onFailure {
                    Timber.e(it)
                    _event.send(SnsEditEvent.LoadFailed)
                }
        }
    }

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
        val unchanged = state.instagramUsername == state.originalInstagramUsername &&
                state.youtubeUsername == state.originalYoutubeUsername
        return unchanged || state.instagramUsernameError != null || state.youtubeUsernameError != null
    }
}
