package com.nexters.boolti.presentation.screen.debug.info

import com.nexters.boolti.domain.model.DeviceInfo
import com.nexters.boolti.domain.model.User
import io.kotest.core.spec.style.DescribeSpec
import io.kotest.matchers.string.shouldContain
import io.kotest.matchers.string.shouldNotContain
import java.time.LocalDateTime

class DebugInfoTextTest : DescribeSpec({
    val deviceInfo = DeviceInfo(
        appVersion = "1.0.0-abc1234",
        model = "Pixel 8",
        os = "Android 15 (SDK 35)",
        locale = "ko_KR",
        timezone = "Asia/Seoul",
        buildType = "debug",
        screen = null,
        storageFree = "3.5GB",
        memory = null,
        battery = "charging 50%",
    )
    val now = LocalDateTime.of(2026, 10, 5, 14, 30)

    describe("buildDebugInfoText") {
        it("로그인한 유저는 서버 ID와 유저코드만 넣고 개인정보는 넣지 않는다") {
            val user = User.My(id = "42", nickname = "불티", email = "a@b.com", userCode = "ABC123")

            val text = buildDebugInfoText(deviceInfo, user, "ShowDetail / showId=1", now)

            text shouldContain "ID: 42"
            text shouldContain "유저코드: ABC123"
            text shouldNotContain "불티"
            text shouldNotContain "a@b.com"
        }

        it("로그인 안 했으면 그렇게 표시한다") {
            buildDebugInfoText(deviceInfo, null, null, now) shouldContain "로그인 안 함"
        }

        it("읽지 못한 값은 알 수 없음으로 표시한다") {
            val text = buildDebugInfoText(deviceInfo, null, null, now)

            text shouldContain "화면: 알 수 없음"
            text shouldContain "메모리: 알 수 없음"
            text shouldContain "[현재 화면]\n알 수 없음"
        }

        it("앱 버전, 기기 상태, 현재 화면을 담는다") {
            val text = buildDebugInfoText(deviceInfo, null, "ShowDetail / showId=1", now)

            text shouldContain "버전: 1.0.0-abc1234"
            text shouldContain "배터리: charging 50%"
            text shouldContain "ShowDetail / showId=1"
            text shouldContain "2026-10-05T14:30:00"
        }
    }
})
