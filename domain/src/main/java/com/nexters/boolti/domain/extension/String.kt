package com.nexters.boolti.domain.extension

import com.nexters.boolti.domain.model.DefaultErrorResponse
import com.nexters.boolti.domain.model.ErrorResponse
import kotlinx.serialization.json.Json

val json = Json {
    prettyPrint = true
    isLenient = true
    ignoreUnknownKeys = true
}

fun String.toErrorResponse(): DefaultErrorResponse {
    return toErrorResponse<DefaultErrorResponse>()
}

inline fun <reified T : ErrorResponse> String.toErrorResponse(): T {
    return json.decodeFromString<T>(this)
}

val String.errorType
    get() = toErrorResponse().type
