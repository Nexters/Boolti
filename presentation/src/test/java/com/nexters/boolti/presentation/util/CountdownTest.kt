package com.nexters.boolti.presentation.util

import io.kotest.core.spec.style.BehaviorSpec
import io.kotest.matchers.shouldBe
import kotlinx.coroutines.test.runTest
import java.time.LocalDateTime

class CountdownTest : BehaviorSpec() {
    init {
        given("목표 시각까지 2.3초 남았다") {
            then("초 경계마다 남은 초를 내보내고 0이 되면 끝난다") {
                runTest {
                    val start = LocalDateTime.of(2026, 1, 1, 0, 0)
                    val now = { start.plusNanos(testScheduler.currentTime * 1_000_000) }
                    val emitted = mutableListOf<Pair<Long, Long>>()

                    countdownFlow(start.plusNanos(2_300_000_000), now).collect {
                        emitted += testScheduler.currentTime to it.seconds
                    }

                    // (가상 시간 ms, 남은 초)
                    emitted shouldBe listOf(0L to 2L, 301L to 1L, 1301L to 0L, 2301L to 0L)
                }
            }
        }
    }
}
