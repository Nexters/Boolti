package com.mangbaam.logger

data class LogData(
    val tag: String?,
    val message: String,
    val level: Int,
    val timestamp: Long = System.currentTimeMillis(),
)
