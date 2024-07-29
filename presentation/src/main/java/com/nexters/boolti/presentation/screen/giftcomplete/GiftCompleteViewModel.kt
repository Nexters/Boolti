package com.nexters.boolti.presentation.screen.giftcomplete

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.viewModelScope
import com.nexters.boolti.domain.repository.GiftRepository
import com.nexters.boolti.presentation.base.BaseViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.stateIn
import javax.inject.Inject

@HiltViewModel
class GiftCompleteViewModel @Inject constructor(
    savedStateHandle: SavedStateHandle,
    giftRepository: GiftRepository,
) : BaseViewModel() {
    private val giftId: String = requireNotNull(savedStateHandle["giftId"]) {
        "giftId 가 전달되지 않았습니다."
    }

    val reservation = giftRepository
        .getGiftPaymentInfo(giftId)
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.Eagerly,
            initialValue = null,
        )
}