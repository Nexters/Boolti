package com.nexters.boolti.data.network.response

import com.nexters.boolti.data.util.toLocalDate
import com.nexters.boolti.data.util.toLocalDateTime
import com.nexters.boolti.domain.model.Show
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import java.time.LocalDate
import java.time.LocalDateTime

@Serializable
internal data class HostedShowDto(
    @SerialName("showId") val showId: String,
    @SerialName("showName") val showName: String,
    @SerialName("date") val date: String,
    @SerialName("salesStartTime") val salesStartDate: String,
    @SerialName("salesEndTime") val salesEndDate: String,
) {
    fun toDomain(): Show = Show(
        id = showId,
        name = showName,
        date = date.toLocalDateTime(),
        salesStartDate = salesStartDate.toLocalDate(),
        salesEndDate = salesEndDate.toLocalDate(),
        thumbnailImage = "",
    )
}
