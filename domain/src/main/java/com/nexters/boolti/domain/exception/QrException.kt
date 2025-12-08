package com.nexters.boolti.domain.exception

data class QrScanException(
    val errorType: QrErrorType?,
) : Exception(errorType?.name)

enum class QrErrorType {
    ShowNotToday, UsedTicket, TicketNotFound, InvalidTicket;

    companion object {
        fun fromString(type: String?): QrErrorType? = when (type?.trim()?.uppercase()) {
            "SHOW_NOT_TODAY" -> ShowNotToday
            "USED_TICKET" -> UsedTicket
            "TICKET_NOT_FOUND" -> TicketNotFound
            else -> null
        }
    }
}
