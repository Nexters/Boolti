package com.nexters.boolti.presentation.util

import androidx.compose.runtime.Composable
import androidx.compose.runtime.State
import androidx.compose.runtime.remember
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import java.time.Duration
import java.time.LocalDateTime
import kotlin.time.Duration.Companion.milliseconds

/**
 * [target]까지 남은 시간을 초 단위로 내보낸다. 0이 되면 끝난다.
 * 매번 [now]로 다시 계산하고 다음 초 경계까지만 기다려서 오차가 쌓이지 않는다.
 */
fun countdownFlow(
    target: LocalDateTime,
    now: () -> LocalDateTime = LocalDateTime::now,
): Flow<Duration> = flow {
    while (true) {
        val left = Duration.between(now(), target)
        emit(left.toWholeSeconds())
        if (left <= Duration.ZERO) break
        delay((left.toMillis() % 1000 + 1).milliseconds)
    }
}

@Composable
fun rememberCountdown(target: LocalDateTime): State<Duration> {
    val initial = remember(target) { Duration.between(LocalDateTime.now(), target).toWholeSeconds() }
    return remember(target) { countdownFlow(target) }.collectAsStateWithLifecycle(initial)
}

private fun Duration.toWholeSeconds(): Duration = Duration.ofSeconds(maxOf(this, Duration.ZERO).seconds)
