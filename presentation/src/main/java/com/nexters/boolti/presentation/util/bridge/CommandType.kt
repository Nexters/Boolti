package com.nexters.boolti.presentation.util.bridge

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
enum class CommandType {
    @SerialName("NAVIGATE_TO_SHOW_DETAIL") NAVIGATE_TO_SHOW_DETAIL,
    @SerialName("NAVIGATE_BACK") NAVIGATE_BACK,
    @SerialName("REQUEST_TOKEN") REQUEST_TOKEN,
    @SerialName("SHOW_TOAST") SHOW_TOAST,
    @SerialName("UNKNOWN") UNKNOWN,
    ;

    companion object {
        fun fromString(value: String): CommandType =
            CommandType.entries.find { it.name == value.trim().uppercase() } ?: UNKNOWN
    }
}
