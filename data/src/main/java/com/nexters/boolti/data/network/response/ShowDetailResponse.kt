package com.nexters.boolti.data.network.response

import com.nexters.boolti.data.util.toLocalDate
import com.nexters.boolti.data.util.toLocalDateTime
import com.nexters.boolti.domain.model.ShowDetail
import kotlinx.serialization.Serializable

@Serializable
internal data class ShowDetailResponse(
    val id: String,
    val name: String,
    val placeName: String,
    val date: String,
    val runningTime: Int,
    val streetAddress: String,
    val detailAddress: String,
    val notice: String,
    val salesStartTime: String,
    val salesEndTime: String,
    val showImg: List<ImageResponse>,
    val hostName: String,
    val hostPhoneNumber: String,
    val reservationStatus: Boolean = false,
    val salesTicketCount: Int,
) {
    fun toDomain(): ShowDetail {
        return ShowDetail(
            id = id,
            name = name,
            placeName = placeName,
            date = date.toLocalDateTime(),
            runningTime = runningTime,
            streetAddress = streetAddress,
            detailAddress = detailAddress,
            notice = notice,
            salesStartDate = salesStartTime.toLocalDate(),
            salesEndDateTime = salesEndTime.toLocalDateTime(),
            images = showImg.toDomains(),
            hostName = hostName,
            hostPhoneNumber = hostPhoneNumber,
            isReserved = reservationStatus,
            salesTicketCount = salesTicketCount,
        )
    }
}
