package com.nexters.boolti.domain.model

import io.kotest.core.spec.style.BehaviorSpec
import io.kotest.matchers.shouldBe
import java.time.LocalDate
import java.time.LocalDateTime

class ShowTest : BehaviorSpec() {
    private val now = LocalDate.now()
    private val standardShow = Show(
        id = "1",
        name = "",
        date = LocalDateTime.MAX,
        salesStartDate = LocalDate.MIN,
        salesEndDate = LocalDate.MAX,
        thumbnailImage = "",
    )

    init {
        given("티켓팅 시작 전인 공연이 주어지고") {
            val ticketingStartDates = listOf(
                now.plusDays(1),
                now.plusDays(30),
                now.plusDays(365),
            )
            val shows = ticketingStartDates.map { standardShow.copy(salesStartDate = it) }

            `when`("공연 상태를 계산했을 때") {
                val results = shows.map { it.state }
                then("Waiting 상태 및 d-day를 반환한다.") {
                    results[0] shouldBe ShowState.WaitingTicketing(1)
                    results[1] shouldBe ShowState.WaitingTicketing(30)
                    results[2] shouldBe ShowState.WaitingTicketing(365)
                }
            }
        }

        given("티켓팅 중인 공연이 주어지고") {
            val randomRange = 1..365
            val ticketingPeriods = listOf(
                Pair(now, now),
                Pair(
                    now.minusDays(randomRange.random().toLong()),
                    now.plusDays(randomRange.random().toLong())
                ),
            )
            val shows = ticketingPeriods.map {
                standardShow.copy(salesStartDate = it.first, salesEndDate = it.second)
            }

            `when`("공연 상태를 계산했을 때") {
                val results = shows.map { it.state }
                then("TicketingInProgress 상태를 반환한다.") {
                    results.forEach {
                        it shouldBe ShowState.TicketingInProgress
                    }
                }
            }
        }

        given("티켓팅이 끝난 공연이 주어지고") {
            val randomRange = 1..365
            val ticketingEndDates = (1..10).map {
                now.minusDays(randomRange.random().toLong())
            }
            val shows = ticketingEndDates.map {
                standardShow.copy(salesEndDate = it)
            }

            `when`("공연 상태를 계산했을 때") {
                val results = shows.map { it.state }
                then("ClosedTicketing 상태를 반환한다.") {
                    results.forEach {
                        it shouldBe ShowState.ClosedTicketing
                    }
                }
            }
        }

        given("끝난 공연이 주어지고") {
            val randomRange = 1..365
            val showDate = (1..10).map {
                LocalDateTime.now().minusDays(randomRange.random().toLong())
            }
            val shows = showDate.map {
                standardShow.copy(date = it)
            }

            `when`("공연 상태를 계산했을 때") {
                val results = shows.map { it.state }
                then("FinishedShow 상태를 반환한다.") {
                    results.forEach {
                        it shouldBe ShowState.FinishedShow
                    }
                }
            }
        }
    }
}
