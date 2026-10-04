package com.nexters.boolti.presentation.component

import io.kotest.core.spec.style.DescribeSpec
import io.kotest.datatest.withData
import io.kotest.matchers.shouldBe

class BooltiHostTest : DescribeSpec({
    describe("불티 도메인과 하위 도메인은 웹뷰 안에서 연다") {
        withData("boolti.in", "preview.boolti.in", "dev.preview.boolti.in", "PREVIEW.BOOLTI.IN") { host ->
            isBooltiHost(host) shouldBe true
        }
    }

    describe("boolti.in을 포함하기만 한 다른 도메인은 막는다") {
        withData("boolti.in.evil.com", "evilboolti.in", "evil.com") { host ->
            isBooltiHost(host) shouldBe false
        }
    }
})
