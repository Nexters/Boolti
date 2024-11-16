package com.nexters.boolti.domain.model

import java.time.Duration
import java.time.LocalDate
import java.time.LocalDateTime

data class Show(
    val id: String,
    val name: String,
    val date: LocalDateTime,
    val salesStartDate: LocalDate,
    val salesEndDate: LocalDate,
    val thumbnailImage: String,
) {
    val state: ShowState
        get() {
            val now = LocalDate.now()

            return when {
                now > date.toLocalDate() -> ShowState.FinishedShow
                now < salesStartDate -> ShowState.WaitingTicketing(
                    Duration.between(
                        LocalDateTime.now(),
                        salesStartDate.atStartOfDay()
                    )
                )

                now <= salesEndDate -> ShowState.TicketingInProgress
                now > salesEndDate -> ShowState.ClosedTicketing
                else -> ShowState.FinishedShow
            }
        }
}
