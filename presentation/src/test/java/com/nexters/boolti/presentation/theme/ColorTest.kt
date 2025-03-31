package com.nexters.boolti.presentation.theme

import androidx.compose.ui.graphics.Color
import io.kotest.core.spec.style.BehaviorSpec
import io.kotest.matchers.shouldBe

class ColorTest : BehaviorSpec() {
    init {
        given("색깔이 주어지고") {
            val grey95 = Color(0xFF090A0B)
            `when`("css 스타일의 색상 문자열로 변환하면") {
                val cssColor = grey95.toCssColor()
                then("css에서 사용할 수 있는 컬러 코드 문자열이 반환된다.") {
                    cssColor shouldBe "#090A0B"
                }
            }
        }
    }
}