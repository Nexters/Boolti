package com.nexters.boolti.domain.model

import java.time.Duration
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
    private val isNonTicketing: Boolean = false,
) {
    val state: ShowState
        get() {
            val now = LocalDateTime.now()

            return when {
                now > date.plusMinutes(runningTime.toLong()) -> ShowState.FinishedShow
                isNonTicketing -> ShowState.NonTicketing // FinishedShow 보다 밑에서 검사해야 함
                now.toLocalDate() < salesStartDate -> ShowState.WaitingTicketing(
                    Duration.between(
                        LocalDateTime.now(),
                        salesStartDate.atStartOfDay()
                    )
                )

                now <= salesEndDateTime -> ShowState.TicketingInProgress
                now > salesEndDateTime -> ShowState.ClosedTicketing
                else -> ShowState.FinishedShow
            }
        }
}
