package com.nexters.boolti.data.network.response

import com.nexters.boolti.data.util.toLocalDate
import com.nexters.boolti.data.util.toLocalDateTime
import com.nexters.boolti.domain.model.Show
import kotlinx.serialization.SerialName

data class PreviousShowsResponse(
    @SerialName("id") val id: String,
    @SerialName("name") val name: String,
    @SerialName("date") val date: String,
    @SerialName("salesStartTime") val salesStartDate: String?,
    @SerialName("salesEndTime") val salesEndDate: String?,
    @SerialName("showImg") val thumbnailImage: String,
    @SerialName("isNonTicketing") val isNonTicketing: Boolean,
) {
    fun toDomain(): Show = Show(
        id = id,
        name = name,
        date = date.toLocalDateTime(),
        salesStartDate = salesStartDate?.toLocalDate(),
        salesEndDate = salesEndDate?.toLocalDate(),
        thumbnailImage = thumbnailImage,
    )
}
