package com.nexters.boolti.data.network.response

import com.nexters.boolti.data.util.toLocalDate
import com.nexters.boolti.data.util.toLocalDateTime
import com.nexters.boolti.domain.model.Show
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
internal data class HostedShowDto(
    @SerialName("id") val showId: String,
    @SerialName("name") val showName: String,
    @SerialName("date") val date: String,
    @SerialName("salesStartTime") val salesStartDate: String?,
    @SerialName("salesEndTime") val salesEndDate: String?,
    @SerialName("showImg") val thumbnailImage: String,
    @SerialName("isNonTicketing") val isNonTicketing: Boolean,
) {
    fun toDomain(): Show = Show(
        id = showId,
        name = showName,
        date = date.toLocalDateTime(),
        salesStartDate = salesStartDate?.toLocalDate(),
        salesEndDate = salesEndDate?.toLocalDate(),
        thumbnailImage = thumbnailImage,
    )
}
