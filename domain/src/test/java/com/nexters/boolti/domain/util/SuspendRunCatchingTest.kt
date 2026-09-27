package com.nexters.boolti.domain.util

import io.kotest.assertions.throwables.shouldThrow
import io.kotest.core.spec.style.BehaviorSpec
import io.kotest.matchers.shouldBe
import io.kotest.matchers.types.shouldBeInstanceOf
import kotlinx.coroutines.TimeoutCancellationException
import kotlinx.coroutines.awaitCancellation
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.withTimeout
import kotlin.time.Duration.Companion.milliseconds
import kotlin.time.Duration.Companion.seconds

class SuspendRunCatchingTest : BehaviorSpec() {
    init {
        given("block 안에서 withTimeout 타임아웃이 발생하면") {
            then("Result.failure 로 반환한다") {
                val result = suspendRunCatching { withTimeout(10.milliseconds) { delay(1.seconds) } }

                result.exceptionOrNull().shouldBeInstanceOf<TimeoutCancellationException>()
            }
        }

        given("바깥 withTimeout 이 만료되면") {
            then("실패로 삼키지 않고 타임아웃을 전파한다") {
                shouldThrow<TimeoutCancellationException> {
                    withTimeout(10.milliseconds) {
                        suspendRunCatching { delay(1.seconds) }
                        error("여기까지 오면 안 된다")
                    }
                }
            }
        }

        given("코루틴이 취소되면") {
            then("취소를 그대로 전파한다") {
                var result: Result<Unit>? = null
                coroutineScope {
                    val job = launch { result = suspendRunCatching { awaitCancellation() } }
                    delay(10.milliseconds)
                    job.cancel()
                }

                result shouldBe null
            }
        }

        given("일반 예외가 발생하면") {
            then("Result.failure 로 반환한다") {
                val result = suspendRunCatching { throw IllegalStateException() }

                result.exceptionOrNull().shouldBeInstanceOf<IllegalStateException>()
            }
        }
    }
}
