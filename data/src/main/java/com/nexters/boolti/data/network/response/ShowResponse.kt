package com.nexters.boolti.data.network.response

import com.nexters.boolti.data.util.toLocalDate
import com.nexters.boolti.data.util.toLocalDateTime
import com.nexters.boolti.domain.model.Show
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
internal data class ShowResponse(
    @SerialName("id")
    val id: String,
    @SerialName("name")
    val name: String,
    @SerialName("date")
    val date: String,
    @SerialName("salesStartTime")
    val salesStartTime: String?,
    @SerialName("salesEndTime")
    val salesEndTime: String?,
    @SerialName("showImg")
    val showImg: String = "",
) {
    fun toDomain(): Show {
        return Show(
            id = id,
            name = name,
            date = date.toLocalDateTime(),
            salesStartDate = salesStartTime?.toLocalDate(),
            salesEndDate = salesEndTime?.toLocalDate(),
            thumbnailImage = showImg,
        )
    }
}
