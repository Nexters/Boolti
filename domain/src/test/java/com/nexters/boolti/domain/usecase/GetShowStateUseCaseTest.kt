package com.nexters.boolti.domain.usecase

import com.nexters.boolti.domain.model.ShowState
import io.kotest.core.spec.style.BehaviorSpec
import io.kotest.matchers.shouldBe
import java.time.LocalDate

class GetShowStateUseCaseTest : BehaviorSpec() {
    private val sut = GetShowStateUseCase()

    init {
        given("티켓팅 시작 전인 공연이 주어지고") {
            val now = LocalDate.now()
            val ticketingStartDates = listOf(
                now.plusDays(1),
                now.plusDays(30),
                now.plusDays(365),
            )
            `when`("공연 상태를 계산했을 때") {
                val results = ticketingStartDates.map {
                    sut(
                        ticketingStartDate = it,
                        ticketingEndDate = LocalDate.MAX,
                        showDate = LocalDate.MAX,
                    )
                }
                then("Waiting 상태 및 d-day를 반환한다.") {
                    results[0] shouldBe ShowState.WaitingTicketing(1)
                    results[1] shouldBe ShowState.WaitingTicketing(30)
                    results[2] shouldBe ShowState.WaitingTicketing(365)
                }
            }
        }

        given("티켓팅 중인 공연이 주어지고") {
            val now = LocalDate.now()
            val randomRange = 1..365
            val ticketingPeriods = listOf(
                Pair(now, now),
                Pair(now.minusDays(randomRange.random().toLong()), now.plusDays(randomRange.random().toLong())),
            )

            `when`("공연 상태를 계산했을 때") {
                val results = ticketingPeriods.map {
                    sut(
                        ticketingStartDate = it.first,
                        ticketingEndDate = it.second,
                        showDate = LocalDate.MAX,
                    )
                }
                then("TicketingInProgress 상태를 반환한다.") {
                    results.forEach {
                        it shouldBe ShowState.TicketingInProgress
                    }
                }
            }
        }

        given("티켓팅이 끝난 공연이 주어지고") {
            val now = LocalDate.now()
            val randomRange = 1..365
            val ticketingEndDates = (1..10).map {
                now.minusDays(randomRange.random().toLong())
            }

            `when`("공연 상태를 계산했을 때") {
                val results = ticketingEndDates.map {
                    sut(
                        ticketingStartDate = LocalDate.MIN,
                        ticketingEndDate = it,
                        showDate = LocalDate.MAX,
                    )
                }
                then("ClosedTicketing 상태를 반환한다.") {
                    results.forEach {
                        it shouldBe ShowState.ClosedTicketing
                    }
                }
            }
        }

        given("끝난 공연이 주어지고") {
            val now = LocalDate.now()
            val randomRange = 1..365
            val showDate = (1..10).map {
                now.minusDays(randomRange.random().toLong())
            }

            `when`("공연 상태를 계산했을 때") {
                val results = showDate.map {
                    sut(
                        ticketingStartDate = LocalDate.MIN,
                        ticketingEndDate = LocalDate.MIN,
                        showDate = it,
                    )
                }
                then("FinishedShow 상태를 반환한다.") {
                    results.forEach {
                        it shouldBe ShowState.FinishedShow
                    }
                }
            }
        }
    }
}