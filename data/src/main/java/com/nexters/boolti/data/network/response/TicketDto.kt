package com.nexters.boolti.data.network.response

import com.nexters.boolti.data.util.toLocalDateTime
import com.nexters.boolti.domain.model.LegacyTicket
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

@Serializable
internal data class TicketDto(
    @SerialName("userId")
    val userId: String,
    @SerialName("ticketId")
    val ticketId: String,
    @SerialName("showName")
    val showName: String,
    @SerialName("placeName")
    val placeName: String,
    @SerialName("showDate")
    val showDate: String,
    @SerialName("showImgPath")
    val showImgPath: String,
    @SerialName("ticketType")
    val ticketType: String,
    @SerialName("ticketName")
    val ticketName: String,
    @SerialName("entryCode")
    val entryCode: String,
    @SerialName("usedAt")
    val usedAt: String? = null, // 사용되지 않았을 때 null
    @SerialName("ticketCreatedAt")
    val ticketCreatedAt: String,
    @SerialName("csTicketId")
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
    @SerialName("userId")
    val userId: String = "",
    @SerialName("showId")
    val showId: String = "",
    @SerialName("ticketId")
    val ticketId: String = "",
    @SerialName("reservationId")
    val reservationId: String = "",
    @SerialName("salesTicketTypeId")
    val salesTicketTypeId: String = "",
    @SerialName("showName")
    val showName: String = "",
    @SerialName("streetAddress")
    val streetAddress: String = "",
    @SerialName("detailAddress")
    val detailAddress: String = "",
    @SerialName("showDate")
    val showDate: String = LocalDateTime.now().format(DateTimeFormatter.ISO_LOCAL_DATE_TIME),
    @SerialName("showImgPath")
    val showImgPath: String = "",
    @SerialName("ticketType")
    val ticketType: String = "",
    @SerialName("ticketName")
    val ticketName: String = "",
    @SerialName("notice")
    val notice: String = "",
    @SerialName("ticketNotice")
    val ticketNotice: String = "",
    @SerialName("placeName")
    val placeName: String = "",
    @SerialName("entryCode")
    val entryCode: String = "",
    @SerialName("usedAt")
    val usedAt: String? = null,
    @SerialName("hostName")
    val hostName: String = "",
    @SerialName("hostPhoneNumber")
    val hostPhoneNumber: String = "",
    @SerialName("csReservationId")
    val csReservationId: String = "",
    @SerialName("csTicketId")
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
