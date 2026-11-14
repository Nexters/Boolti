package com.nexters.boolti.data.network.response

import com.nexters.boolti.data.util.toLocalDate
import com.nexters.boolti.data.util.toLocalDateTime
import com.nexters.boolti.domain.model.ShowDetail
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
internal data class ShowDetailResponse(
    @SerialName("id")
    val id: String,
    @SerialName("name")
    val name: String,
    @SerialName("concertHallId")
    val placeId: String? = null,
    @SerialName("placeName")
    val placeName: String,
    @SerialName("date")
    val date: String,
    @SerialName("runningTime")
    val runningTime: Int,
    @SerialName("streetAddress")
    val streetAddress: String,
    @SerialName("detailAddress")
    val detailAddress: String,
    @SerialName("notice")
    val notice: String,
    @SerialName("salesStartTime")
    val salesStartTime: String?,
    @SerialName("salesEndTime")
    val salesEndTime: String?,
    @SerialName("showImg")
    val showImg: List<ImageResponse>,
    @SerialName("hostName")
    val hostName: String,
    @SerialName("hostPhoneNumber")
    val hostPhoneNumber: String,
    @SerialName("reservationStatus")
    val reservationStatus: Boolean = false,
    @SerialName("salesTicketCount")
    val salesTicketCount: Int = 0,
    @SerialName("isNonTicketing")
    val isNonTicketing: Boolean = false,
) {
    fun toDomain(): ShowDetail {
        return ShowDetail(
            id = id,
            name = name,
            placeId = placeId,
            placeName = placeName,
            date = date.toLocalDateTime(),
            runningTime = runningTime,
            streetAddress = streetAddress,
            detailAddress = detailAddress,
            notice = notice,
            salesStartDate = salesStartTime?.toLocalDate(),
            salesEndDateTime = salesEndTime?.toLocalDateTime(),
            images = showImg.toDomains(),
            hostName = hostName,
            hostPhoneNumber = hostPhoneNumber,
            isReserved = reservationStatus,
            salesTicketCount = salesTicketCount,
            isNonTicketing = isNonTicketing,
        )
    }
}
