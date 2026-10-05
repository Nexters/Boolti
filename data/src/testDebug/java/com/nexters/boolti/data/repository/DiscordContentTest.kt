package com.nexters.boolti.data.repository

import io.kotest.core.spec.style.DescribeSpec
import io.kotest.matchers.ints.shouldBeLessThanOrEqual
import io.kotest.matchers.shouldBe
import io.kotest.matchers.string.shouldEndWith

class DiscordContentTest : DescribeSpec({
    describe("toDiscordContent") {
        it("짧은 글은 코드 블록으로만 감싼다") {
            toDiscordContent("버전: 1.0.0") shouldBe "```\n버전: 1.0.0\n```"
        }

        it("긴 글은 2000자 안으로 자르고 잘렸다고 표시한다") {
            val content = toDiscordContent("가".repeat(5000))

            content.length shouldBeLessThanOrEqual 2000
            content shouldEndWith "…\n```"
        }

        it("딱 맞는 길이는 자르지 않는다") {
            val content = toDiscordContent("a".repeat(1992))

            content.length shouldBe 2000
            content shouldEndWith "a\n```"
        }
    }
})
