package com.nexters.boolti.presentation.screen.debug.info

import com.nexters.boolti.domain.model.DeviceInfo
import com.nexters.boolti.domain.model.User
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

private const val UNKNOWN = "알 수 없음"

/**
 * 팝업에 보이고 그대로 복사되는 디버그 정보. 토큰·이메일·닉네임 같은 개인정보는 넣지 않는다
 */
internal fun buildDebugInfoText(
    deviceInfo: DeviceInfo,
    user: User.My?,
    currentScreen: String?,
    now: LocalDateTime,
): String = buildString {
    appendLine("[시각] ${now.format(DateTimeFormatter.ISO_LOCAL_DATE_TIME)}")
    appendLine()
    appendLine("[앱]")
    appendLine("버전: ${deviceInfo.appVersion ?: UNKNOWN}")
    appendLine("빌드: ${deviceInfo.buildType}")
    appendLine()
    appendLine("[유저]")
    if (user == null) {
        appendLine("로그인 안 함")
    } else {
        appendLine("ID: ${user.id}")
        appendLine("유저코드: ${user.userCode.ifEmpty { UNKNOWN }}")
    }
    appendLine()
    appendLine("[기기]")
    appendLine("모델: ${deviceInfo.model}")
    appendLine("OS: ${deviceInfo.os}")
    appendLine("언어: ${deviceInfo.locale}")
    appendLine("시간대: ${deviceInfo.timezone}")
    appendLine("화면: ${deviceInfo.screen ?: UNKNOWN}")
    appendLine()
    appendLine("[상태]")
    appendLine("남은 저장공간: ${deviceInfo.storageFree ?: UNKNOWN}")
    appendLine("메모리: ${deviceInfo.memory ?: UNKNOWN}")
    appendLine("배터리: ${deviceInfo.battery ?: UNKNOWN}")
    appendLine()
    appendLine("[현재 화면]")
    append(currentScreen ?: UNKNOWN)
}
