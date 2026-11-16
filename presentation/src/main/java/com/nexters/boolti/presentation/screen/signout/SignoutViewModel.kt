package com.nexters.boolti.presentation.screen.signout

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.nexters.boolti.domain.repository.AuthRepository
import com.nexters.boolti.presentation.R
import com.nexters.boolti.domain.request.SignoutRequest
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.launch
import timber.log.Timber
import java.io.IOException
import javax.inject.Inject

@HiltViewModel
class SignoutViewModel @Inject constructor(
    private val repository: AuthRepository,
) : ViewModel() {
    private val _reason = MutableStateFlow("")
    val reason = _reason.asStateFlow()

    private val _event = Channel<SignoutEvent>()
    val event = _event.receiveAsFlow()

    fun signout() {
        viewModelScope.launch {
            repository.signout(
                request = SignoutRequest(reason.value)
            ).onSuccess {
                event(SignoutEvent.SignoutSuccess)
            }.onFailure { e ->
                Timber.e(e)
                val message = if (e is IOException) R.string.error_network else R.string.message_unknown_error
                event(SignoutEvent.SignoutFailed(message))
            }
        }
    }

    fun setReason(reason: String) {
        _reason.value = reason
    }

    private fun event(event: SignoutEvent) {
        viewModelScope.launch {
            _event.send(event)
        }
    }
}
