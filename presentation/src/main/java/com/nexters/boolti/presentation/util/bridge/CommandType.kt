package com.nexters.boolti.presentation.util.bridge

import kotlinx.serialization.Serializable

@Serializable
enum class CommandType {
    NAVIGATE_TO_SHOW_DETAIL,
    NAVIGATE_BACK,
    REQUEST_TOKEN,
    SHOW_TOAST,
    UNKNOWN;

    companion object {
        fun fromString(value: String): CommandType =
            CommandType.entries.find { it.name == value.trim().uppercase() } ?: UNKNOWN
    }
}
