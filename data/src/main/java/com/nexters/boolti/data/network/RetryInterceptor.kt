package com.nexters.boolti.data.network

import okhttp3.Interceptor
import okhttp3.Response
import retrofit2.Invocation
import java.io.IOException
import kotlin.random.Random
import kotlin.time.Duration
import kotlin.time.Duration.Companion.milliseconds
import kotlin.time.Duration.Companion.seconds

/**
 * GET 요청이지만 5xx 재시도를 하면 안 되는 API 에 붙인다.
 */
@Target(AnnotationTarget.FUNCTION)
@Retention(AnnotationRetention.RUNTIME)
annotation class DoNotRetry

/**
 * GET 요청이 5xx 응답을 받으면 지수적 백오프(대기 시간을 2배씩 늘림)로 재시도한다.
 *
 * 결제·PUT 처럼 한 번만 실행돼야 하는 요청은 GET 이 아니므로 재시도하지 않는다.
 * 대기 시간에는 jitter(0~50% 랜덤 추가)를 넣어 여러 기기가 동시에 재시도하지 않게 한다.
 * 응답에 `Retry-After`(초)가 있으면 그 값만큼 기다리고, [maxRetryAfter] 보다 길면 재시도하지 않는다.
 */
internal class RetryInterceptor(
    private val maxRetries: Int = 3,
    private val initialDelay: Duration = 500.milliseconds,
    private val maxRetryAfter: Duration = 5.seconds,
) : Interceptor {
    override fun intercept(chain: Interceptor.Chain): Response {
        val request = chain.request()
        val doNotRetry = request.tag(Invocation::class.java)?.method()?.isAnnotationPresent(DoNotRetry::class.java) == true
        if (doNotRetry || request.method != "GET") return chain.proceed(request)

        var response = chain.proceed(request)
        var delay = initialDelay
        for (attempt in 1..maxRetries) {
            if (response.code < 500 || chain.call().isCanceled()) break
            val retryAfter = response.header("Retry-After")?.toLongOrNull()?.seconds
            if (retryAfter != null && retryAfter > maxRetryAfter) break
            response.close()
            Thread.sleep((retryAfter ?: delay.withJitter()).inWholeMilliseconds)
            if (chain.call().isCanceled()) throw IOException("Canceled")
            delay *= 2
            response = chain.proceed(request)
        }
        return response
    }
}

private fun Duration.withJitter(): Duration = this * (1 + Random.nextDouble(0.5))
