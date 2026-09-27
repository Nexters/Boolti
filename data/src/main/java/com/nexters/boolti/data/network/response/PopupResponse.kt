package com.nexters.boolti.data.network.response

import com.nexters.boolti.data.util.toLocalDateTime
import com.nexters.boolti.domain.model.Popup
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
internal data class PopupResponse(
    @SerialName("id")
    val id: String,
    @SerialName("type")
    val type: PopupType,
    @SerialName("eventUrl")
    val eventUrl: String?,
    @SerialName("view")
    val view: String?,
    @SerialName("noticeTitle")
    val noticeTitle: String?,
    @SerialName("description")
    val description: String,
    @SerialName("startDate")
    val startDate: String,
    @SerialName("endDate")
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
    @SerialName("EVENT") EVENT,
    @SerialName("NOTICE") NOTICE,
}