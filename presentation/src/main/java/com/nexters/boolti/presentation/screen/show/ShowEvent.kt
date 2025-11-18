package com.nexters.boolti.presentation.screen.show

import com.nexters.boolti.domain.model.Popup

sealed interface ShowEvent {
    data class ShowPopup(
        val popup: Popup
    ) : ShowEvent
}
