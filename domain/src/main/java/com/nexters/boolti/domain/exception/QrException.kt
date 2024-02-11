package com.nexters.boolti.domain.exception

data class QrScanException(
    val errorType: QrErrorType?,
) : Exception(errorType?.name)

enum class QrErrorType {
    SHOW_NOT_TODAY;

    companion object {
        fun fromString(type: String?): QrErrorType? = QrErrorType.entries.find {
            type?.trim()?.uppercase() == it.name
        }
    }
}
