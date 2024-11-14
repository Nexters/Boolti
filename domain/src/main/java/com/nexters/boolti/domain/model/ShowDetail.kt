package com.nexters.boolti.domain.model

import java.time.LocalDate
import java.time.LocalDateTime

data class ShowDetail(
    val id: String = "0",
    val name: String = "",
    val placeName: String = "",
    val date: LocalDateTime = LocalDateTime.now(),
    val runningTime: Int = 0,
    val streetAddress: String = "",
    val detailAddress: String = "",
    val notice: String = "",
    val salesStartDate: LocalDate = LocalDate.now(),
    val salesEndDateTime: LocalDateTime = LocalDateTime.now(),
    val images: List<ImagePair> = emptyList(),
    val hostName: String = "",
    val hostPhoneNumber: String = "",
    val isReserved: Boolean = false,
    val salesTicketCount: Int = 0,
) {
    val state: ShowState
        get() {
            val now = LocalDate.now()

            return when {
                now > date.toLocalDate() -> ShowState.FinishedShow
                now < salesStartDate -> ShowState.WaitingTicketing(salesStartDate.atStartOfDay())
                now <= salesEndDateTime.toLocalDate() -> ShowState.TicketingInProgress
                now > salesEndDateTime.toLocalDate() -> ShowState.ClosedTicketing
                else -> ShowState.FinishedShow
            }
        }
}
