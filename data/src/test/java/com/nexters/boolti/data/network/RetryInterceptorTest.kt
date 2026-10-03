package com.nexters.boolti.data.network

import io.kotest.assertions.throwables.shouldThrow
import io.kotest.core.spec.style.DescribeSpec
import io.kotest.matchers.shouldBe
import okhttp3.OkHttpClient
import okhttp3.Protocol
import okhttp3.Request
import okhttp3.RequestBody.Companion.toRequestBody
import okhttp3.Response
import okhttp3.ResponseBody.Companion.toResponseBody
import retrofit2.Invocation
import java.io.IOException
import kotlin.concurrent.thread
import kotlin.time.Duration
import kotlin.time.Duration.Companion.milliseconds

class RetryInterceptorTest : DescribeSpec({
    var count = 0

    fun call(
        method: String,
        codes: List<Int>,
        doNotRetry: Boolean = false,
        retryAfter: String? = null,
        initialDelay: Duration = 1.milliseconds,
        cancelAfter: Duration? = null,
    ): Pair<Int, Int> {
        count = 0
        val client = OkHttpClient.Builder()
            .addInterceptor(RetryInterceptor(maxRetries = 2, initialDelay = initialDelay))
            .addInterceptor { chain ->
                val code = codes[minOf(count++, codes.lastIndex)]
                Response.Builder()
                    .request(chain.request())
                    .protocol(Protocol.HTTP_1_1)
                    .code(code)
                    .message("")
                    .apply { retryAfter?.let { header("Retry-After", it) } }
                    .body("".toResponseBody())
                    .build()
            }
            .build()
        val javaMethod = Api::class.java.getMethod(if (doNotRetry) "doNotRetry" else "retry")
        val request = Request.Builder()
            .url("http://localhost/")
            .method(method, if (method == "GET") null else "".toRequestBody())
            .tag(Invocation::class.java, Invocation.of(javaMethod, emptyList<Any>()))
            .build()
        val call = client.newCall(request)
        cancelAfter?.let { thread { Thread.sleep(it.inWholeMilliseconds); call.cancel() } }
        val code = call.execute().use { it.code }
        return code to count
    }

    describe("GET 요청") {
        it("5xx 후 성공하면 성공 응답을 반환한다") {
            call("GET", listOf(500, 503, 200)) shouldBe (200 to 3)
        }

        it("maxRetries 만큼만 재시도한다") {
            call("GET", listOf(500)) shouldBe (500 to 3)
        }

        it("4xx 는 재시도하지 않는다") {
            call("GET", listOf(404)) shouldBe (404 to 1)
        }

        it("Retry-After 가 있으면 그 값을 따라 재시도한다") {
            call("GET", listOf(503, 200), retryAfter = "0") shouldBe (200 to 2)
        }

        it("Retry-After 가 최대 대기 시간보다 길면 재시도하지 않는다") {
            call("GET", listOf(503), retryAfter = "100") shouldBe (503 to 1)
        }

        it("대기 중에 취소되면 다시 요청하지 않는다") {
            shouldThrow<IOException> {
                call("GET", listOf(500), initialDelay = 500.milliseconds, cancelAfter = 100.milliseconds)
            }
            count shouldBe 1
        }
    }

    describe("재시도 대상이 아닌 요청") {
        it("GET 이 아니면 재시도하지 않는다") {
            call("PUT", listOf(500)) shouldBe (500 to 1)
            call("POST", listOf(500)) shouldBe (500 to 1)
        }

        it("@DoNotRetry 가 붙으면 재시도하지 않는다") {
            call("GET", listOf(500), doNotRetry = true) shouldBe (500 to 1)
        }
    }
}) {
    interface Api {
        fun retry()

        @DoNotRetry
        fun doNotRetry()
    }
}
