package com.nexters.boolti.domain.repository

/**
 * QA 디버그 정보 전송. 구현은 debug 빌드에만 있다
 */
interface DebugReportRepository {
    /** 전송할 곳이 설정돼 있는지 */
    val canSend: Boolean

    suspend fun send(content: String, logs: String, screenshotPath: String?): Result<Unit>
}
