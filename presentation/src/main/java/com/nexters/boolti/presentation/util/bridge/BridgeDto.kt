package com.nexters.boolti.presentation.util.bridge

import kotlinx.serialization.Serializable
import kotlinx.serialization.json.JsonElement

@Serializable
data class BridgeDto(
    val id: String,
    val command: CommandType,
    val timestamp: Long = System.currentTimeMillis(),
    val data: JsonElement? = null,
)
