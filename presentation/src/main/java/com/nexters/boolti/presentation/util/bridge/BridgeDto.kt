package com.nexters.boolti.presentation.util.bridge

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.JsonElement

@Serializable
data class BridgeDto(
    @SerialName("id")
    val id: String,
    @SerialName("command")
    val command: CommandType,
    @SerialName("timestamp")
    val timestamp: Long = System.currentTimeMillis(),
    @SerialName("data")
    val data: JsonElement? = null,
)
