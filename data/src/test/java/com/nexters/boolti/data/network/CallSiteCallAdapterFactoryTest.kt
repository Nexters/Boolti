package com.nexters.boolti.data.network

import io.kotest.assertions.throwables.shouldThrow
import io.kotest.core.spec.style.BehaviorSpec
import io.kotest.matchers.nulls.shouldBeNull
import io.kotest.matchers.nulls.shouldNotBeNull
import io.kotest.matchers.shouldBe
import okhttp3.OkHttpClient
import okhttp3.Protocol
import okhttp3.ResponseBody
import okhttp3.ResponseBody.Companion.toResponseBody
import retrofit2.HttpException
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.create
import retrofit2.http.GET
import okhttp3.Response as RawResponse

class CallSiteCallAdapterFactoryTest : BehaviorSpec() {
    private interface TestService {
        @GET("body")
        suspend fun body(): ResponseBody

        @GET("response")
        suspend fun response(): Response<ResponseBody>
    }

    private fun service(code: Int): TestService {
        val client = OkHttpClient.Builder()
            .addInterceptor { chain ->
                RawResponse.Builder()
                    .request(chain.request())
                    .protocol(Protocol.HTTP_1_1)
                    .code(code)
                    .message("")
                    .body("""{"type":"T"}""".toResponseBody())
                    .build()
            }
            .build()
        return Retrofit.Builder()
            .baseUrl("http://localhost/")
            .client(client)
            .addCallAdapterFactory(CallSiteCallAdapterFactory())
            .build()
            .create()
    }

    init {
        given("에러 응답이 오면") {
            `when`("body를 반환하는 suspend 함수는") {
                then("HttpException의 요청 tag에 호출 위치 스택이 붙는다") {
                    val e = shouldThrow<HttpException> { service(400).body() }
                    val callSite = e.response()?.raw()?.request?.tag(ApiCallSite::class.java)
                    callSite.shouldNotBeNull()
                    callSite.stackTrace.any { it.className.contains("CallSiteCallAdapterFactoryTest") } shouldBe true
                }
            }

            `when`("Response를 반환하는 suspend 함수는") {
                then("에러 응답을 그대로 돌려주고 body도 읽을 수 있다") {
                    val response = service(400).response()
                    response.code() shouldBe 400
                    response.errorBody()?.string() shouldBe """{"type":"T"}"""
                }
            }
        }

        given("성공 응답이 오면") {
            then("body를 그대로 돌려주고 tag를 붙이지 않는다") {
                val response = service(200).response()
                response.body()?.string() shouldBe """{"type":"T"}"""
                response.raw().request.tag(ApiCallSite::class.java).shouldBeNull()
            }
        }
    }
}
