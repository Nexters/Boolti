package com.nexters.boolti.presentation.screen.gift

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.viewModelScope
import com.nexters.boolti.domain.model.ImagePair
import com.nexters.boolti.domain.repository.GiftRepository
import com.nexters.boolti.domain.repository.TicketingRepository
import com.nexters.boolti.domain.request.FreeGiftRequest
import com.nexters.boolti.domain.request.OrderIdRequest
import com.nexters.boolti.domain.request.TicketingInfoRequest
import com.nexters.boolti.domain.usecase.GetRefundPolicyUsecase
import com.nexters.boolti.domain.usecase.GetUserUsecase
import com.nexters.boolti.presentation.base.BaseViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.collections.immutable.toPersistentList
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onCompletion
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.onStart
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.coroutines.plus
import javax.inject.Inject

@HiltViewModel
class GiftViewModel @Inject constructor(
    savedStateHandle: SavedStateHandle,
    getUserUseCase: GetUserUsecase,
    private val ticketingRepository: TicketingRepository,
    private val giftRepository: GiftRepository,
    private val getRefundPolicyUseCase: GetRefundPolicyUsecase,
) : BaseViewModel() {
    private val userId = checkNotNull(getUserUseCase()?.id) {
        "[GiftViewModel] 사용자 정보가 없습니다."
    }
    val showId: String = requireNotNull(savedStateHandle["showId"])
    val salesTicketTypeId: String = requireNotNull(savedStateHandle["salesTicketId"])
    private val ticketCount: Int = savedStateHandle["ticketCount"] ?: 1

    private val _uiState = MutableStateFlow(GiftUiState())
    val uiState: StateFlow<GiftUiState> = _uiState.asStateFlow()

    private val _events = Channel<GiftEvent>()
    val events = _events.receiveAsFlow()

    fun toggleAgreement() {
        _uiState.update { it.toggleAgreement() }
    }

    init {
        load()
    }

    private fun load() {
        getRefundPolicyUseCase().onEach { refundPolicy ->
            _uiState.update {
                it.copy(refundPolicy = refundPolicy.toPersistentList())
            }
        }.launchIn(viewModelScope + recordExceptionHandler)

        giftRepository.getGiftImages()
            .onEach { images ->
                _uiState.update {
                    it.copy(
                        giftImages = images.toPersistentList(),
                        selectedImage = images.first()
                    )
                }
            }
            .launchIn(viewModelScope + recordExceptionHandler)

        ticketingRepository.getTicketingInfo(
            TicketingInfoRequest(showId, salesTicketTypeId, ticketCount)
        ).onStart {
            _uiState.update { it.copy(loading = true) }
        }.onEach { info ->
            _uiState.update {
                it.copy(
                    poster = info.showImg,
                    showDate = info.showDate,
                    showName = info.showName,
                    ticketName = info.saleTicketName,
                    ticketCount = info.ticketCount,
                    totalPrice = info.totalPrice,
                )
            }
        }.onCompletion {
            _uiState.update {
                it.copy(loading = false)
            }
        }.launchIn(viewModelScope + recordExceptionHandler)
    }

    fun updateMessage(message: String) {
        _uiState.update { it.copy(message = message) }
    }

    fun updateSenderName(name: String) {
        _uiState.update { it.copy(senderName = name) }
    }

    fun updateSenderContact(contact: String) {
        _uiState.update { it.copy(senderContact = contact) }
    }

    fun updateReceiverName(name: String) {
        _uiState.update { it.copy(receiverName = name) }
    }

    fun updateReceiverContact(contact: String) {
        _uiState.update { it.copy(receiverContact = contact) }
    }

    fun selectImage(image: ImagePair) {
        _uiState.update { it.copy(selectedImage = image) }
    }

    fun pay() {
        val state = uiState.value

        if (state.isFree) {
            giftRepository.sendFreeGift(
                FreeGiftRequest(
                    amount = 0,
                    showId = showId,
                    salesTicketTypeId = salesTicketTypeId,
                    ticketCount = state.ticketCount,
                    senderName = state.senderName,
                    senderPhoneNumber = state.senderContact,
                    recipientName = state.receiverName,
                    recipientPhoneNumber = state.receiverContact,
                    message = state.message,
                    giftImageId = state.selectedImage?.id ?: state.giftImages.first().id
                )
            )
                .onStart { _uiState.update { it.copy(loading = true) } }
                .onEach { giftPayment ->
                    sendEvent(GiftEvent.GiftSuccess(giftId = giftPayment.giftId))
                }
                .onCompletion { _uiState.update { it.copy(loading = false) } }
                .launchIn(viewModelScope + recordExceptionHandler)

            return
        }

        ticketingRepository.requestOrderId(OrderIdRequest(showId, salesTicketTypeId, ticketCount))
            .onStart { _uiState.update { it.copy(loading = true) } }
            .onEach { orderId ->
                sendEvent(GiftEvent.ProgressPayment(userId, orderId))
            }
            .onCompletion { _uiState.update { it.copy(loading = false) } }
            .launchIn(viewModelScope + recordExceptionHandler)
    }

    private fun sendEvent(event: GiftEvent) {
        viewModelScope.launch {
            _events.send(event)
        }
    }
}
