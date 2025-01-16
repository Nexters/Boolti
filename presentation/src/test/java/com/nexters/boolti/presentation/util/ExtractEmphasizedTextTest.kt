package com.nexters.boolti.presentation.util

import com.nexters.boolti.presentation.extension.extractEmphasizedText
import io.kotest.core.spec.style.BehaviorSpec
import io.kotest.matchers.shouldBe

class ExtractEmphasizedTextTest : BehaviorSpec() {
    init {
        given("백틱이 포함된 문자열이 주어진다.") {
            val targetString = "`강조된 부분` 강조되지 않은 부분"

            `when`("이 문자열에서 강조된 부분을 분리한다.") {
                val (emphasizedText, content) = targetString.extractEmphasizedText()

                then("강조된 부분과 아닌 부분을 분리한다.") {
                    emphasizedText shouldBe "강조된 부분"
                    content shouldBe "강조되지 않은 부분"
                }
            }
        }

        given("백틱이 포함된 문자열이 두 번 주어진다.") {
            val targetString = "`강조된 부분` 강조되지 않은 부분 `나는 페이크`"

            `when`("이 문자열에서 강조된 부분을 분리한다.") {
                val (emphasizedText, content) = targetString.extractEmphasizedText()

                then("첫 번째 백틱만 강조 텍스트로 분리한다.") {
                    emphasizedText shouldBe "강조된 부분"
                    content shouldBe "강조되지 않은 부분 `나는 페이크`"
                }
            }
        }

        given("백틱이 포함된 문자열이 안 주어진다.") {
            val targetString = "강조되지 않은 부분"

            `when`("이 문자열에서 강조된 부분을 분리한다.") {
                val (emphasizedText, content) = targetString.extractEmphasizedText()

                then("강조된 부분이 없다.") {
                    emphasizedText shouldBe ""
                    content shouldBe targetString
                }
            }
        }
    }
}