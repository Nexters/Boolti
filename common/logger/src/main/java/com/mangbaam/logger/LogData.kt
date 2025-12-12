package com.mangbaam.logger

import java.util.UUID

data class LogData(
    private val _id: String? = null,
    val tag: String?,
    val message: String,
    val level: Int,
    val timestamp: Long = System.currentTimeMillis(),
) {
    val id: String
        get() = _id ?: UUID.randomUUID().toString()
}
