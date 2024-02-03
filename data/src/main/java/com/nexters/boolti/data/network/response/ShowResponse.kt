package com.nexters.boolti.data.network.response

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
//            date = LocalDate(date),
//            salesStartTime = salesStartTime,
//            salesEndTime = salesEndTime,
            thumbnailImage = showImg,
        )
    }
}

fun List<ShowResponse>.toDomains(): List<Show> = this.map { it.toDomain() }
