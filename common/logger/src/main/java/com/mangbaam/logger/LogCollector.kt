package com.mangbaam.logger

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.map
import kotlin.concurrent.atomics.AtomicLong
import kotlin.concurrent.atomics.ExperimentalAtomicApi
import kotlin.concurrent.atomics.incrementAndFetch

object LogCollector {
    private const val MAX_LOGS = 1000 // 최대 로그 개수

    @OptIn(ExperimentalAtomicApi::class)
    private val idSequence = AtomicLong(0L)

    private val logQueue = ArrayDeque<LogData>(MAX_LOGS)
    private val defaultExcludeTags = setOf(
        "View", "DecorView",
    )

    private var excludeTags: Set<String> = defaultExcludeTags

    private val _logs = MutableStateFlow<List<LogData>>(emptyList())
    val allLogs: StateFlow<List<LogData>> = _logs.asStateFlow()

    fun setExcludeTags(tags: Set<String>) {
        excludeTags = tags + defaultExcludeTags
    }

    fun getLogsByTag(tags: Set<String> = emptySet(), exactMatch: Boolean = false): Flow<List<LogData>> =
        if (tags.isEmpty()) {
            allLogs
        } else {
            allLogs.map { logList ->
                logList.filter { logData ->
                    if (exactMatch) {
                        tags.contains(logData.tag)
                    } else {
                        tags.any { tag -> logData.tag?.contains(tag, ignoreCase = true) == true }
                    }
                }
            }
        }

    @OptIn(ExperimentalAtomicApi::class)
    @Synchronized
    fun addLog(log: LogData) {
        // 최대 개수 초과 시 가장 오래된 로그 제거 (FIFO)
        if (logQueue.size >= MAX_LOGS) {
            logQueue.removeFirst()
        }
        val atomicId = idSequence.incrementAndFetch().toString()
        logQueue.addLast(log.copy(_id = atomicId))

        // 불변 리스트로 변환하여 StateFlow 업데이트
        _logs.value = logQueue.toList()
    }

    @Synchronized
    fun clear() {
        logQueue.clear()
        _logs.value = emptyList()
    }

    @Synchronized
    fun getLogCount(): Int = logQueue.size
}
