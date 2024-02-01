package com.nexters.boolti.domain.usecase

import com.nexters.boolti.domain.model.ShowState
import java.time.LocalDate
import javax.inject.Inject

class GetShowStateUseCase @Inject constructor() {
    // fixme : Show data class 가 모델링 된 후 수정하기
    operator fun invoke(
        ticketingStartDate: LocalDate,
        ticketingEndDate: LocalDate,
        showDate: LocalDate,
    ): ShowState {
        val now = LocalDate.now()
        val dDay = ticketingStartDate.toEpochDay() - now.toEpochDay()

        return when {
            now < ticketingStartDate -> ShowState.WaitingTicketing(dDay.toInt())
            now <= ticketingEndDate -> ShowState.TicketingInProgress
            now > showDate -> ShowState.FinishedShow
            now > ticketingEndDate -> ShowState.ClosedTicketing
            else -> ShowState.FinishedShow
        }
    }
}