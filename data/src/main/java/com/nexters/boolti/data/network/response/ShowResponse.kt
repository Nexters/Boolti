package com.nexters.boolti.data.network.response

import com.nexters.boolti.data.util.toLocalDate
import com.nexters.boolti.data.util.toLocalDateTime
import com.nexters.boolti.domain.model.Show
import kotlinx.serialization.Serializable

@Serializable
data class ShowResponse(
    val id: Long,
    val name: String,
    val date: String,
    val salesStartTime: String,
    val salesEndTime: String,
    val showImg: String,
) {
    fun toDomain(): Show {
        return Show(
            id = id,
            name = name,
            date = date.toLocalDateTime(),
            salesStartDate = salesStartTime.toLocalDate(),
            salesEndDate = salesEndTime.toLocalDate(),
            thumbnailImage = showImg,
        )
    }
}

fun List<ShowResponse>.toDomains(): List<Show> = this.map { it.toDomain() }
