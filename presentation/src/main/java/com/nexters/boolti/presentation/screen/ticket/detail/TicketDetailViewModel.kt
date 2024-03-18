package com.nexters.boolti.presentation.screen.ticket.detail

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.nexters.boolti.domain.exception.ManagerCodeException
import com.nexters.boolti.domain.repository.TicketRepository
import com.nexters.boolti.domain.request.ManagerCodeRequest
import com.nexters.boolti.domain.usecase.GetRefundPolicyUsecase
import com.nexters.boolti.presentation.base.BaseViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Job
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.flow.singleOrNull
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.coroutines.plus
import javax.inject.Inject

@HiltViewModel
class TicketDetailViewModel @Inject constructor(
    savedStateHandle: SavedStateHandle,
    private val repository: TicketRepository,
    private val getRefundPolicyUsecase: GetRefundPolicyUsecase,
) : BaseViewModel() {
    private val ticketId: String = requireNotNull(savedStateHandle["ticketId"]) {
        "TicketDetailViewModel 에 ticketId 가 전달되지 않았습니다."
    }

    private val _uiState = MutableStateFlow(TicketDetailUiState())
    val uiState = _uiState.asStateFlow()

    private val _managerCodeState = MutableStateFlow(ManagerCodeState())
    val managerCodeState = _managerCodeState.asStateFlow()

    private val _event = Channel<TicketDetailEvent>()
    val event = _event.receiveAsFlow()

    init {
        load()
    }

    private fun load(): Job {
        return viewModelScope.launch(recordExceptionHandler) {
            repository.getTicket(ticketId).singleOrNull()?.let { ticket ->
                _uiState.update { it.copy(ticket = ticket) }
            }

            getRefundPolicyUsecase().onEach { refundPolicy ->
                _uiState.update { it.copy(refundPolicy = refundPolicy) }
            }.launchIn(viewModelScope + recordExceptionHandler)
        }
    }

    fun refresh() = load()

    fun requestEntrance(managerCode: String) {
        val ticket = uiState.value.ticket
        viewModelScope.launch(recordExceptionHandler) {
            repository.requestEntrance(
                ManagerCodeRequest(showId = ticket.showId, ticketId = ticket.ticketId, managerCode = managerCode)
            ).catch { e ->
                when (e) {
                    is ManagerCodeException -> {
                        _managerCodeState.update { it.copy(error = e.errorType) }
                    }
                    else -> throw e
                }
            }.singleOrNull()?.let {
                event(TicketDetailEvent.ManagerCodeValid)
                load().invokeOnCompletion { // Refresh
                    event(TicketDetailEvent.OnRefresh)
                }
            }
        }
    }

    fun setManagerCode(code: String) {
        _managerCodeState.update { it.copy(code = code, error = null) }
    }

    private fun event(event: TicketDetailEvent) {
        viewModelScope.launch {
            _event.send(event)
        }
    }
}
