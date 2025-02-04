package com.nexters.boolti.data.network.response

import com.nexters.boolti.data.util.toLocalDateTime
import com.nexters.boolti.domain.model.Popup
import kotlinx.serialization.Serializable

@Serializable
internal data class PopupResponse(
    val id: String,
    val type: PopupType,
    val eventUrl: String?,
    val view: String?,
    val noticeTitle: String?,
    val description: String,
    val startDate: String,
    val endDate: String,
) {
    fun toDomain(): Popup = when (type) {
        PopupType.EVENT -> Popup.Event(
            id = id,
            startDate = startDate.toLocalDateTime(),
            endDate = endDate.toLocalDateTime(),
            imageUrl = description,
            eventUrl = eventUrl,
        )

        PopupType.NOTICE -> Popup.Notice(
            id = id,
            startDate = startDate.toLocalDateTime(),
            endDate = endDate.toLocalDateTime(),
            title = noticeTitle ?: "",
            description = description,
        )
    }
}


@Serializable
enum class PopupType {
    EVENT,
    NOTICE,
}