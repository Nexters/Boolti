package com.nexters.boolti.presentation.screen.debug.info

import android.util.Log
import com.mangbaam.logger.LogData
import java.time.Instant
import java.time.ZoneId
import java.time.format.DateTimeFormatter

private val logTimeFormatter = DateTimeFormatter.ofPattern("MM-dd HH:mm:ss.SSS")

/** 디스코드에 .txt로 첨부할 로그. `시각 레벨/태그: 메시지` 한 줄씩 */
internal fun buildLogText(logs: List<LogData>, zoneId: ZoneId): String =
    logs.joinToString("\n") { log ->
        val time = logTimeFormatter.format(Instant.ofEpochMilli(log.timestamp).atZone(zoneId))
        "$time ${log.level.toLevelChar()}/${log.tag.orEmpty()}: ${log.message}"
    }

private fun Int.toLevelChar(): Char = when (this) {
    Log.VERBOSE -> 'V'
    Log.DEBUG -> 'D'
    Log.INFO -> 'I'
    Log.WARN -> 'W'
    Log.ERROR -> 'E'
    Log.ASSERT -> 'A'
    else -> '?'
}
