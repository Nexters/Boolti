package com.nexters.boolti.presentation.screen.giftcomplete

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.viewModelScope
import com.nexters.boolti.domain.repository.GiftRepository
import com.nexters.boolti.domain.repository.TicketingRepository
import com.nexters.boolti.presentation.base.BaseViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.stateIn
import javax.inject.Inject

@HiltViewModel
class GiftCompleteViewModel @Inject constructor(
    savedStateHandle: SavedStateHandle,
    ticketingRepository: TicketingRepository,
    giftRepository: GiftRepository,
) : BaseViewModel() {
    private val reservationId: String = requireNotNull(savedStateHandle["reservationId"]) {
        "reservationId 가 전달되지 않았습니다."
    }
    private val giftId: String = requireNotNull(savedStateHandle["giftId"]) {
        "giftId 가 전달되지 않았습니다."
    }

    val reservation = ticketingRepository
        .getPaymentInfo(reservationId)
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.Eagerly,
            initialValue = null,
        )

    val gift = giftRepository.getGift(giftId)
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.Eagerly,
            initialValue = null,
        )
}