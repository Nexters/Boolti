package com.nexters.boolti.domain.model

import java.time.LocalDateTime

sealed interface Popup {
    val id: String
    val startDate: LocalDateTime
    val endDate: LocalDateTime

    data class Event(
        override val id: String,
        override val startDate: LocalDateTime,
        override val endDate: LocalDateTime,
        val imageUrl: String,
        val eventUrl: String?,
    ) : Popup

    data class Notice(
        override val id: String,
        override val startDate: LocalDateTime,
        override val endDate: LocalDateTime,
        val title: String,
        val description: String,
    ) : Popup
}
