package com.nexters.boolti.data.network.response


import com.nexters.boolti.data.util.toLocalDateTime
import com.nexters.boolti.domain.model.TicketGroup
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import java.time.LocalDateTime

@Serializable
internal data class TicketsDto(
    @SerialName("userId")
    val userId: String? = null,
    @SerialName("reservationId")
    val reservationId: String? = null,
    @SerialName("showName")
    val showName: String? = null,
    @SerialName("placeName")
    val placeName: String? = null,
    @SerialName("showDate")
    val showDate: String? = null,
    @SerialName("showImgPath")
    val showImgPath: String? = null,
    @SerialName("ticketType")
    val ticketType: String? = null,
    @SerialName("ticketName")
    val ticketName: String? = null,
    @SerialName("ticketCount")
    val ticketCount: Int? = null,
) {
    fun toDomain(): TicketGroup = TicketGroup(
        userId = userId ?: "",
        showId = "",
        reservationId = reservationId ?: "",
        showName = showName ?: "",
        placeName = placeName ?: "",
        streetAddress = "",
        detailAddress = "",
        showDate = showDate?.toLocalDateTime() ?: LocalDateTime.now(),
        notice = "",
        ticketNotice = "",
        poster = showImgPath ?: "",
        ticketType = TicketGroup.TicketType.convert(ticketType),
        ticketName = ticketName ?: "",
        hostName = "",
        hostPhoneNumber = "",
        tickets = List(ticketCount ?: 0) {
            TicketGroup.Ticket(
                ticketId = "",
                entryCode = "",
                usedAt = null,
                ticketCreatedAt = LocalDateTime.MIN,
                csTicketId = "",
                showDate = LocalDateTime.MIN,
            )
        }
    )
}

@Serializable
internal data class TicketGroupDto(
    @SerialName("detailAddress")
    val detailAddress: String? = null,
    @SerialName("hostName")
    val hostName: String? = null,
    @SerialName("hostPhoneNumber")
    val hostPhoneNumber: String? = null,
    @SerialName("notice")
    val notice: String? = null,
    @SerialName("placeName")
    val placeName: String? = null,
    @SerialName("reservationId")
    val reservationId: String? = null,
    @SerialName("showDate")
    val showDate: String? = null,
    @SerialName("showId")
    val showId: String? = null,
    @SerialName("showImgPath")
    val showImgPath: String? = null,
    @SerialName("showName")
    val showName: String? = null,
    @SerialName("streetAddress")
    val streetAddress: String? = null,
    @SerialName("ticketName")
    val ticketName: String? = null,
    @SerialName("ticketNotice")
    val ticketNotice: String? = null,
    @SerialName("ticketType")
    val ticketType: String? = null,
    @SerialName("tickets")
    val tickets: List<TicketDto>? = null,
    @SerialName("userId")
    val userId: String? = null,
) {
    fun toDomain(): TicketGroup = TicketGroup(
        userId = userId ?: "",
        showId = showId ?: "",
        reservationId = reservationId ?: "",
        showName = showName ?: "",
        placeName = placeName ?: "",
        streetAddress = streetAddress ?: "",
        detailAddress = detailAddress ?: "",
        showDate = showDate?.toLocalDateTime() ?: LocalDateTime.MIN,
        notice = notice ?: "",
        ticketNotice = ticketNotice ?: "",
        poster = showImgPath ?: "",
        ticketType = TicketGroup.TicketType.convert(ticketType),
        ticketName = ticketName ?: "",
        hostName = hostName ?: "",
        hostPhoneNumber = hostPhoneNumber ?: "",
        tickets = tickets?.map {
            it.toDomain(showDate = showDate?.toLocalDateTime() ?: LocalDateTime.MIN)
        } ?: emptyList(),
    )

    @Serializable
    data class TicketDto(
        @SerialName("csTicketId")
        val csTicketId: String? = null,
        @SerialName("entryCode")
        val entryCode: String? = null,
        @SerialName("ticketCreatedAt")
        val ticketCreatedAt: String? = null,
        @SerialName("ticketId")
        val ticketId: String? = null,
        @SerialName("usedAt")
        val usedAt: String? = null,
    ) {
        fun toDomain(showDate: LocalDateTime): TicketGroup.Ticket = TicketGroup.Ticket(
            ticketId = ticketId ?: "",
            entryCode = entryCode ?: "",
            usedAt = usedAt?.toLocalDateTime(),
            ticketCreatedAt = ticketCreatedAt?.toLocalDateTime() ?: LocalDateTime.MIN,
            csTicketId = csTicketId ?: "",
            showDate = showDate,
        )
    }
}
