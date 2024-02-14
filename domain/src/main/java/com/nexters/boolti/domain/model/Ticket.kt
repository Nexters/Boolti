package com.nexters.boolti.domain.model

import java.time.LocalDateTime

data class Ticket(
    val userId: String = "",
    val showId: String = "",
    val ticketId: String = "",
    val reservationId: String = "",
    val salesTicketTypeId: String = "",
    val showName: String = "",
    val streetAddress: String = "",
    val detailAddress: String = "",
    val showDate: LocalDateTime = LocalDateTime.now(),
    val poster: String = "",
    val isInviteTicket: Boolean = false,
    val ticketName: String = "",
    val notice: String = "",
    val placeName: String = "",
    val entryCode: String = "",
    val usedAt: LocalDateTime? = null,
    val hostName: String = "",
    val hostPhoneNumber: String = "",
) {
    val ticketState: TicketState
        get() = run {
            val now = LocalDateTime.now()
            when {
                now > showDate -> TicketState.Finished
                usedAt == null -> TicketState.Ready
                now > usedAt -> TicketState.Used
                else -> TicketState.Ready
            }
        }
}

enum class TicketState {
    Ready, Used, Finished
}
