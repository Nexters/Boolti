package com.nexters.boolti.domain.model

import java.time.LocalDate
import java.time.LocalDateTime

data class Show(
    val id: String,
    val name: String,
    val date: LocalDateTime,
    val salesStartDate: LocalDate?,
    val salesEndDate: LocalDate?,
    val thumbnailImage: String,
) {
    fun state(today: LocalDate = LocalDate.now()): ShowState =
        when {
            today > date.toLocalDate() -> ShowState.FinishedShow
            salesStartDate == null || salesEndDate == null -> ShowState.NonTicketing
            today < salesStartDate -> ShowState.WaitingTicketing(salesStartDate.atStartOfDay())

            today <= salesEndDate -> ShowState.TicketingInProgress
            today > salesEndDate -> ShowState.ClosedTicketing
            else -> ShowState.FinishedShow
        }
}
