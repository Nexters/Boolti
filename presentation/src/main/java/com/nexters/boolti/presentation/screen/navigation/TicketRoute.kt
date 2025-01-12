package com.nexters.boolti.presentation.screen.navigation

import kotlinx.serialization.Serializable

sealed interface TicketRoute {
    @Serializable
    data class TicketRoot(
        val ticketId: String,
    ) : TicketRoute

    @Serializable
    data object TicketDetail : TicketRoute

    @Serializable
    data object Qr : TicketRoute
}
