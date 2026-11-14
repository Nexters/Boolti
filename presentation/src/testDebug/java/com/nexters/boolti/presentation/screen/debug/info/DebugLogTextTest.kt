package com.nexters.boolti.presentation.screen.debug.info

import android.util.Log
import com.mangbaam.logger.LogData
import io.kotest.core.spec.style.DescribeSpec
import io.kotest.matchers.shouldBe
import java.time.LocalDateTime
import java.time.ZoneId

class DebugLogTextTest : DescribeSpec({
    val zone = ZoneId.of("Asia/Seoul")
    val timestamp = LocalDateTime.of(2026, 10, 5, 14, 30, 1, 234_000_000)
        .atZone(zone).toInstant().toEpochMilli()

    describe("buildLogText") {
        it("한 줄에 시각, 레벨, 태그, 메시지를 담는다") {
            val logs = listOf(
                LogData(tag = "OkHttp", message = "--> GET /papi/v1/shows", level = Log.DEBUG, timestamp = timestamp),
                LogData(tag = null, message = "실패", level = Log.ERROR, timestamp = timestamp),
            )

            buildLogText(logs, zone) shouldBe
                "10-05 14:30:01.234 D/OkHttp: --> GET /papi/v1/shows\n" +
                "10-05 14:30:01.234 E/: 실패"
        }

        it("모르는 레벨은 ?로 표시한다") {
            val logs = listOf(LogData(tag = "T", message = "m", level = 99, timestamp = timestamp))

            buildLogText(logs, zone) shouldBe "10-05 14:30:01.234 ?/T: m"
        }

        it("로그가 없으면 빈 문자열") {
            buildLogText(emptyList(), zone) shouldBe ""
        }
    }
})
