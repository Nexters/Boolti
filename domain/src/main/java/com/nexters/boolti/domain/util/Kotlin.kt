package com.nexters.boolti.domain.util

import kotlinx.coroutines.TimeoutCancellationException
import kotlinx.coroutines.currentCoroutineContext
import kotlinx.coroutines.ensureActive
import kotlin.coroutines.cancellation.CancellationException

// TODO: 나중에 이 util 패키지는 별도 모듈로 뺴보자
suspend inline fun <T> suspendRunCatching(block: suspend () -> T): Result<T> {
    return try {
        Result.success(block())
    } catch (e: TimeoutCancellationException) {
        // 바깥 코루틴이 취소된 상태면 취소를 전파하고, block 안의 withTimeout 타임아웃만 실패로 처리
        currentCoroutineContext().ensureActive()
        Result.failure(e)
    } catch (e: CancellationException) {
        throw e
    } catch (e: Throwable) {
        Result.failure(e)
    }
}
