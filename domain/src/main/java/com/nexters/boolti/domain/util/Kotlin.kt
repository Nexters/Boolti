package com.nexters.boolti.domain.util

import kotlin.coroutines.cancellation.CancellationException

// TODO: 나중에 이 util 패키지는 별도 모듈로 뺴보자
suspend inline fun <T> suspendRunCatching(block: suspend () -> T): Result<T> {
    return try {
        Result.success(block())
    } catch (e: CancellationException) {
        throw e
    } catch (e: Throwable) {
        Result.failure(e)
    }
}
