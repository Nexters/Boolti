package com.nexters.boolti.presentation.screen

import com.nexters.boolti.presentation.base.BaseViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.receiveAsFlow
import javax.inject.Inject

@HiltViewModel
class GiftDeepLinkViewModel @Inject constructor() : BaseViewModel() {
    private val _pendingGiftEvents = Channel<String>(Channel.BUFFERED)
    val pendingGiftEvents = _pendingGiftEvents.receiveAsFlow()

    fun pendGift(giftUuid: String) {
        _pendingGiftEvents.trySend(giftUuid)
    }
}