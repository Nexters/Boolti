package com.nexters.boolti.domain.model

import java.time.LocalDateTime

/**
 * @property userId
 * @property showId
 * @property ticketId
 * @property reservationId
 * @property salesTicketTypeId
 * @property showName
 * @property streetAddress
 * @property detailAddress
 * @property showDate
 * @property poster
 * @property isInviteTicket
 * @property ticketName
 * @property notice 공연 내용 (공연 상세에서 사용)
 * @property ticketNotice 안내사항 for 주최자
 * @property placeName
 * @property entryCode QR 에 담길 정보
 * @property usedAt
 * @property hostName
 * @property hostPhoneNumber
 */
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
    val ticketNotice: String = "",
    val placeName: String = "",
    val entryCode: String = "",
    val usedAt: LocalDateTime? = null,
    val hostName: String = "",
    val hostPhoneNumber: String = "",
    val csReservationId: String = "",
    val csTicketId: String = "",
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

enum class TicketState {
    Ready, Used, Finished
}
