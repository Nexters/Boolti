package com.nexters.boolti.domain.util

import kotlinx.coroutines.currentCoroutineContext
import kotlinx.coroutines.ensureActive
import kotlin.coroutines.cancellation.CancellationException

// TODO: 나중에 이 util 패키지는 별도 모듈로 뺴보자
suspend inline fun <T> suspendRunCatching(block: suspend () -> T): Result<T> {
    return try {
        Result.success(block())
    } catch (e: CancellationException) {
        // 현재 코루틴이 취소된 경우만 전파하고, 안쪽 withTimeout 타임아웃·다른 코루틴의 취소는 실패로 처리
        currentCoroutineContext().ensureActive()
        Result.failure(e)
    } catch (e: Throwable) {
        Result.failure(e)
    }
}
