package com.nexters.boolti.domain.extension

import com.nexters.boolti.domain.model.DefaultErrorResponse
import com.nexters.boolti.domain.model.QrScanErrorResponse
import io.kotest.core.spec.style.BehaviorSpec
import io.kotest.matchers.shouldBe

class StringTest : BehaviorSpec() {
    init {
        given("서버 에러 응답 JSON이 주어지고") {
            val body = """{"errorTraceId":"trace-1","type":"SHOW_NOT_FOUND","detail":"공연이 없어요"}"""

            `when`("기본 에러 응답으로 변환하면") {
                then("모든 필드가 채워진다") {
                    body.toErrorResponse() shouldBe DefaultErrorResponse(
                        errorTraceId = "trace-1",
                        type = "SHOW_NOT_FOUND",
                        detail = "공연이 없어요",
                    )
                }
            }

            `when`("에러 타입만 꺼내면") {
                then("type 값이 반환된다") {
                    body.errorType shouldBe "SHOW_NOT_FOUND"
                }
            }
        }

        given("모르는 필드가 섞인 에러 응답이 주어지고") {
            val body = """{"errorTraceId":"trace-2","type":"UNKNOWN","detail":"","extra":1}"""

            `when`("기본 에러 응답으로 변환하면") {
                then("모르는 필드는 무시하고 변환된다") {
                    body.toErrorResponse().type shouldBe "UNKNOWN"
                }
            }
        }

        given("QR 스캔 에러 응답 JSON이 주어지고") {
            val body = """{"errorTraceId":"trace-3","type":"INVALID_QR","showName":"불티 공연","detail":"잘못된 QR"}"""

            `when`("QR 스캔 에러 응답으로 변환하면") {
                then("공연 이름까지 채워진다") {
                    body.toErrorResponse<QrScanErrorResponse>().showName shouldBe "불티 공연"
                }
            }
        }
    }
}
