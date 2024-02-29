package com.nexters.boolti.data.network.response

import com.nexters.boolti.domain.model.Show
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import java.time.LocalDate
import java.time.LocalDateTime

@Serializable
internal data class HostedShowDto(
    @SerialName("showId") val showId: String,
    @SerialName("showName") val showName: String,
) {
    fun toDomain(): Show = Show(
        id = showId,
        name = showName,
        date = LocalDateTime.now(),
        salesStartDate = LocalDate.now(),
        salesEndDate = LocalDate.now(),
        thumbnailImage = "",
    )
}
