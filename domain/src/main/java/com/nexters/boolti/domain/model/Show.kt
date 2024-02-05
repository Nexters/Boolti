package com.nexters.boolti.domain.model

import java.time.LocalDate
import java.time.LocalDateTime

data class Show(
    val id: Long,
    val name: String,
    val date: LocalDateTime,
    val salesStartDate: LocalDate,
    val salesEndDate: LocalDate,
    val thumbnailImage: String,
) {
    val state: ShowState
        get() {
            val now = LocalDate.now()
            val dDay = salesStartDate.toEpochDay() - now.toEpochDay()

            return when {
                now > date.toLocalDate() -> ShowState.FinishedShow
                now < salesStartDate -> ShowState.WaitingTicketing(dDay.toInt())
                now <= salesEndDate -> ShowState.TicketingInProgress
                now > salesEndDate -> ShowState.ClosedTicketing
                else -> ShowState.FinishedShow
            }
        }
}
