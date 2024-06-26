package com.nexters.boolti.presentation.util

import io.kotest.core.spec.style.BehaviorSpec
import io.kotest.matchers.shouldBe

class UrlParserTest : BehaviorSpec() {
    init {
        given("url이 포함된 문자열이 주어지고") {
            val targetString = "https://www.naver.com 적당한https://www.naver.com 문자열 http://www.naver.com"

            `when`("이 문자열을 파싱하면") {
                val urlParser = UrlParser(targetString)
                val urlOffsets = urlParser.urlOffsets

                then("문자열 내 url의 start, end offset을 확인할 수 있다") {
                    urlOffsets.size shouldBe 3
                    urlOffsets[0] shouldBe UrlOffset(0, 21)
                    urlOffsets[1] shouldBe UrlOffset(25, 46)
                    urlOffsets[2] shouldBe UrlOffset(51, 71)
                }
            }
        }
    }
}