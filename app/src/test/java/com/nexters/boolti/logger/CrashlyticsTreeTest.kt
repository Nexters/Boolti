package com.nexters.boolti.logger

import android.util.Log
import io.kotest.core.spec.style.BehaviorSpec
import io.kotest.matchers.maps.shouldBeEmpty
import io.kotest.matchers.shouldBe
import okhttp3.Protocol
import okhttp3.Request
import okhttp3.RequestBody.Companion.toRequestBody
import okhttp3.ResponseBody.Companion.toResponseBody
import retrofit2.HttpException
import retrofit2.Response
import okhttp3.Response as RawResponse
import java.io.IOException
import kotlin.coroutines.cancellation.CancellationException

class CrashlyticsTreeTest : BehaviorSpec() {
    init {
        given("ERROR 로그에 예외가 주어지고") {
            then("일반 예외는 기록하고, IOException·CancellationException은 제외한다") {
                CrashlyticsTree.shouldRecord(Log.ERROR, IllegalStateException()) shouldBe true
                CrashlyticsTree.shouldRecord(Log.ERROR, IOException()) shouldBe false
                CrashlyticsTree.shouldRecord(Log.ERROR, CancellationException()) shouldBe false
            }
        }

        given("ERROR 미만 로그에 예외가 주어지고") {
            then("기록하지 않는다") {
                CrashlyticsTree.shouldRecord(Log.WARN, IllegalStateException()) shouldBe false
            }
        }

        given("HttpException이 주어지고") {
            val errorBody = """{"errorTraceId":"trace-1","type":"NO_REMAINING_QUANTITY","detail":"d"}"""

            `when`("GET 요청이면") {
                then("쿼리까지 http_api에 남기고, 에러 응답의 type과 errorTraceId를 남긴다") {
                    CrashlyticsTree.httpErrorKeys(httpException("GET", "https://a.com/api/shows?page=0&size=10", 404, errorBody)) shouldBe mapOf(
                        "http_api" to "GET /api/shows?page=0&size=10 404",
                        "error_type" to "NO_REMAINING_QUANTITY",
                        "error_trace_id" to "trace-1",
                    )
                }
            }

            `when`("GET 쿼리에 허용 목록 밖의 이름이 있으면") {
                then("그 값은 가린다") {
                    CrashlyticsTree.httpErrorKeys(httpException("GET", "https://a.com/api/search?keyword=010-1234&inviteCode=abc&page=0", 400, errorBody))["http_api"] shouldBe
                        "GET /api/search?keyword=***&inviteCode=***&page=0 400"
                }
            }

            `when`("GET이 아닌 요청이면") {
                then("쿼리는 남기지 않는다") {
                    CrashlyticsTree.httpErrorKeys(httpException("POST", "https://a.com/api/order?x=1", 400, errorBody))["http_api"] shouldBe
                        "POST /api/order 400"
                }
            }

            `when`("에러 응답이 서버 에러 형식이 아니면") {
                then("http_api만 남긴다") {
                    CrashlyticsTree.httpErrorKeys(httpException("GET", "https://a.com/api/shows", 502, "<html></html>")) shouldBe mapOf(
                        "http_api" to "GET /api/shows 502",
                    )
                }
            }
        }

        given("HttpException이 아닌 예외가 주어지고") {
            then("키를 남기지 않는다") {
                CrashlyticsTree.httpErrorKeys(IllegalStateException()).shouldBeEmpty()
            }
        }
    }

    private fun httpException(method: String, url: String, code: Int, body: String): HttpException {
        val requestBody = if (method == "GET") null else "".toRequestBody()
        val raw = RawResponse.Builder()
            .request(Request.Builder().url(url).method(method, requestBody).build())
            .protocol(Protocol.HTTP_1_1)
            .code(code)
            .message("error")
            .build()
        return HttpException(Response.error<Unit>(body.toResponseBody(), raw))
    }
}
