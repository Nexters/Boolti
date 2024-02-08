package com.nexters.boolti.data.network.response

import com.nexters.boolti.data.util.toLocalDateTime
import com.nexters.boolti.domain.model.Ticket
import kotlinx.serialization.Serializable
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

@Serializable
data class TicketDto(
    val userId: String,
    val ticketId: String,
    val showName: String,
    val placeName: String,
    val showDate: String,
    val showImgPath: String,
    val ticketType: String,
    val ticketName: String,
    val entryCode: String,
    val usedAt: String? = null, // 사용되지 않았을 때 null
) {
    fun toDomain(): Ticket = Ticket(
        userId = userId,
        ticketId = ticketId,
        showName = showName,
        showDate = showDate.toLocalDateTime(),
        poster = showImgPath,
        isInviteTicket = ticketType.trim().uppercase() == "INVITE",
        ticketName = ticketName,
        placeName = placeName,
        entryCode = entryCode,
        usedAt = usedAt?.toLocalDateTime(),
    )
}

@Serializable
data class TicketDetailDto(
    val userId: String = "",
    val showId: String = "",
    val ticketId: String = "",
    val reservationId: String = "",
    val salesTicketTypeId: String = "",
    val showName: String = "",
    val streetAddress: String = "",
    val detailAddress: String = "",
    val showDate: String = LocalDateTime.now().format(DateTimeFormatter.ISO_LOCAL_DATE_TIME),
    val showImgPath: String = "",
    val ticketType: String = "",
    val ticketName: String = "",
    val notice: String = "",
    val placeName: String = "",
    val entryCode: String = "",
    val usedAt: String? = null,
    val hostName: String = "",
    val hostPhoneNumber: String = "",
) {
    fun toDomain(): Ticket = Ticket(
        userId = userId,
        showId = showId,
        ticketId = ticketId,
        reservationId = reservationId,
        salesTicketTypeId = salesTicketTypeId,
        showName = showName,
        streetAddress = streetAddress,
        detailAddress = detailAddress,
        showDate = showDate.toLocalDateTime(),
        poster = showImgPath,
        isInviteTicket = ticketType.trim().uppercase() == "INVITE",
        ticketName = ticketName,
        notice = notice,
        placeName = placeName,
        entryCode = entryCode,
        usedAt = usedAt?.toLocalDateTime(),
        hostName = hostName,
        hostPhoneNumber = hostPhoneNumber,
    )
}
