package com.nexters.boolti.domain.model

import kotlinx.serialization.Serializable

interface ErrorResponse {
    val errorTraceId: String
    val type: String
    val detail: String
}

@Serializable
data class DefaultErrorResponse(
    override val errorTraceId: String,
    override val type: String,
    override val detail: String,
) : ErrorResponse

@Serializable
data class QrScanErrorResponse(
    override val errorTraceId: String,
    override val type: String,
    val showName: String,
    override val detail: String,
) : ErrorResponse
