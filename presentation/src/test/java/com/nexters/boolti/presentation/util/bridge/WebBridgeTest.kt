package com.nexters.boolti.presentation.util.bridge

import io.kotest.assertions.throwables.shouldThrow
import io.kotest.core.spec.style.DescribeSpec
import io.kotest.matchers.collections.shouldBeEmpty
import io.kotest.matchers.collections.shouldHaveSize
import io.kotest.matchers.shouldBe
import io.kotest.matchers.shouldNotBe
import kotlinx.coroutines.CompletableDeferred
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.Job
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import kotlinx.coroutines.test.runTest
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.JsonElement
import kotlinx.serialization.json.JsonNull
import kotlinx.serialization.json.jsonObject
import kotlinx.serialization.json.jsonPrimitive

@Serializable
private data class NotCommand(val message: String)

@OptIn(ExperimentalCoroutinesApi::class)
class WebBridgeTest : DescribeSpec({

    fun bridge(register: WebBridge.() -> Unit) = WebBridge(scope = CoroutineScope(Job()), register = register)

    fun message(command: String, data: String? = null) =
        """{"id":"1","timestamp":"1759000000000","command":"$command"${data?.let { ""","data":$it""" } ?: ""}}"""

    fun String.responseData(): JsonElement? = Json.parseToJsonElement(this).jsonObject["data"]

    describe("dispatch") {
        it("어노테이션의 이름으로 핸들러를 찾아 data를 타입으로 넘긴다") {
            var received: ShowToast? = null
            val bridge = bridge { handle { t: ShowToast -> received = t } }

            bridge.dispatch(message("SHOW_TOAST", """{"message":"안녕"}"""))

            received shouldBe ShowToast("안녕")
        }

        it("핸들러의 반환값이 응답 data가 된다") {
            val bridge = bridge { handle { _: RequestToken -> TokenResponse("abc") } }

            val response = bridge.dispatch(message("REQUEST_TOKEN"))

            response?.responseData()?.jsonObject?.get("token")?.jsonPrimitive?.content shouldBe "abc"
        }

        it("Unit을 반환하면 응답 data는 null이다") {
            val bridge = bridge { handle { _: ShowToast -> } }

            val response = bridge.dispatch(message("SHOW_TOAST", """{"message":"안녕"}"""))

            response shouldNotBe null
            response?.responseData() shouldBe JsonNull
        }

        it("응답에 요청의 id와 command를 담는다") {
            val bridge = bridge { handle { _: ShowToast -> } }

            val response = Json.parseToJsonElement(
                bridge.dispatch(message("SHOW_TOAST", """{"message":"안녕"}""")).orEmpty(),
            ).jsonObject

            response["id"]?.jsonPrimitive?.content shouldBe "1"
            response["command"]?.jsonPrimitive?.content shouldBe "SHOW_TOAST"
        }

        it("모르는 키는 무시한다") {
            var received: ShowToast? = null
            val bridge = bridge { handle { t: ShowToast -> received = t } }

            bridge.dispatch(message("SHOW_TOAST", """{"message":"안녕","extra":[1,2]}"""))

            received shouldBe ShowToast("안녕")
        }

        it("등록되지 않은 커맨드는 응답하지 않는다") {
            val bridge = bridge { handle { _: ShowToast -> } }

            bridge.dispatch(message("UNKNOWN_COMMAND")) shouldBe null
        }

        it("필수 값이 없으면 핸들러를 부르지 않고 응답하지 않는다") {
            var called = false
            val bridge = bridge { handle { _: ShowToast -> called = true } }

            val response = bridge.dispatch(message("SHOW_TOAST", "{}"))

            response shouldBe null
            called shouldBe false
        }

        it("핸들러가 실패하면 응답하지 않는다") {
            val bridge = bridge { handle<ShowToast, Unit> { error("실패") } }

            bridge.dispatch(message("SHOW_TOAST", """{"message":"안녕"}""")) shouldBe null
        }

        it("JSON 형식이 아니면 응답하지 않는다") {
            val bridge = bridge { handle { _: ShowToast -> } }

            bridge.dispatch("not json") shouldBe null
        }
    }

    describe("handle") {
        it("@WebBridgeCommand가 없는 타입은 등록할 수 없다") {
            shouldThrow<IllegalArgumentException> {
                bridge { handle { _: NotCommand -> } }
            }
        }

        it("같은 커맨드를 두 번 등록할 수 없다") {
            shouldThrow<IllegalArgumentException> {
                bridge {
                    handle { _: ShowToast -> }
                    handle { _: ShowToast -> }
                }
            }
        }
    }

    describe("receive") {
        it("응답을 기다리는 커맨드가 있어도 다른 커맨드는 먼저 처리된다") {
            runTest(UnconfinedTestDispatcher()) {
                val token = CompletableDeferred<String>()
                val sent = mutableListOf<String>()
                val bridge = WebBridge(backgroundScope) {
                    handle { _: RequestToken -> TokenResponse(token.await()) }
                    handle { _: ShowToast -> }
                }.apply { attach { sent += it } }

                bridge.receive(message("REQUEST_TOKEN"))
                sent.shouldBeEmpty()

                bridge.receive(message("SHOW_TOAST", """{"message":"안녕"}"""))
                sent shouldHaveSize 1
                Json.parseToJsonElement(sent[0]).jsonObject["command"]?.jsonPrimitive?.content shouldBe "SHOW_TOAST"

                token.complete("abc")
                sent shouldHaveSize 2
                sent[1].responseData()?.jsonObject?.get("token")?.jsonPrimitive?.content shouldBe "abc"
            }
        }
    }
})
