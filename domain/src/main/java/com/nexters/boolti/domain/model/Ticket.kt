package com.nexters.boolti.domain.model

import java.time.LocalDateTime

data class Ticket(
    val userId: String,
    val reservationId: String,
    val showName: String,
    val placeName: String,
    val showDate: LocalDateTime,
    val poster: String,
    val ticketType: TicketGroup.TicketType,
    val ticketName: String,
    val ticketCount: Int,
)

data class TicketGroup(
    val userId: String,
    val showId: String,
    val reservationId: String,
    val showName: String,
    val placeName: String,
    val streetAddress: String,
    val detailAddress: String,
    val showDate: LocalDateTime,
    val notice: String,
    val ticketNotice: String,
    val poster: String,
    val ticketType: TicketType,
    val ticketName: String,
    val hostName: String,
    val hostPhoneNumber: String,
    val tickets: List<Ticket>,
) {
    data class Ticket(
        val ticketId: String,
        val entryCode: String,
        val usedAt: LocalDateTime?,
        val ticketCreatedAt: LocalDateTime,
        val csTicketId: String,
        val showDate: LocalDateTime,
    ) {
        val ticketState: TicketState
            get() = run {
                val now = LocalDateTime.now()
                when {
                    usedAt != null && now > usedAt -> TicketState.Used
                    now.toLocalDate() > showDate.toLocalDate() -> TicketState.Finished
                    else -> TicketState.Ready
                }
            }
    }

    enum class TicketType {
        Unknown, Sale, Invite;

        companion object {
            fun convert(type: String?): TicketType = when (type?.trim()?.uppercase()) {
                "SALE", "SALES" -> Sale
                "INVITE", "INVITED" -> Invite
                else -> Unknown
            }
        }
    }
}
