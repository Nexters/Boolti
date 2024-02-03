package com.nexters.boolti.data.network.response

import com.nexters.boolti.domain.model.Show
import kotlinx.serialization.Serializable

@Serializable
data class ShowResponse(
    val id: Long,
    val name: String,
    val date: Long,
    val salesStartTime: Long,
    val salesEndTime: Long,
    val showImg: String,
) {
    fun toDomain(): Show {
        return Show(
            name = name
        )
    }
}

fun List<ShowResponse>.toDomains(): List<Show> = this.map { it.toDomain() }
