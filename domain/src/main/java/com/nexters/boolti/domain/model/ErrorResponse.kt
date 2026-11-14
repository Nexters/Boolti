package com.nexters.boolti.domain.model

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

interface ErrorResponse {
    val errorTraceId: String
    val type: String
    val detail: String
}

@Serializable
data class DefaultErrorResponse(
    @SerialName("errorTraceId")
    override val errorTraceId: String,
    @SerialName("type")
    override val type: String,
    @SerialName("detail")
    override val detail: String,
) : ErrorResponse

@Serializable
data class QrScanErrorResponse(
    @SerialName("errorTraceId")
    override val errorTraceId: String,
    @SerialName("type")
    override val type: String,
    @SerialName("showName")
    val showName: String,
    @SerialName("detail")
    override val detail: String,
) : ErrorResponse
