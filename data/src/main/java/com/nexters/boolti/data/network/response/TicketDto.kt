package com.nexters.boolti.data.network.response

import com.nexters.boolti.data.util.toLocalDateTime
import com.nexters.boolti.domain.model.LegacyTicket
import kotlinx.serialization.Serializable
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

@Serializable
internal data class TicketDto(
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
    val ticketCreatedAt: String,
    val csTicketId: String,
) {
    fun toDomain(): LegacyTicket = LegacyTicket(
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
        csReservationId = "", // TODO 이거 빈 값으로 둬도 되는지 확인 필요
        csTicketId = csTicketId,
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
    val ticketNotice: String = "",
    val placeName: String = "",
    val entryCode: String = "",
    val usedAt: String? = null,
    val hostName: String = "",
    val hostPhoneNumber: String = "",
    val csReservationId: String = "",
    val csTicketId: String = "",
) {
    fun toDomain(): LegacyTicket = LegacyTicket(
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
        ticketNotice = ticketNotice,
        placeName = placeName,
        entryCode = entryCode,
        usedAt = usedAt?.toLocalDateTime(),
        hostName = hostName,
        hostPhoneNumber = hostPhoneNumber,
        csReservationId = csReservationId,
        csTicketId = csTicketId,
    )
}
