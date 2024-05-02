package com.nexters.boolti.domain.exception

data class TicketingException(
    val errorType: TicketingErrorType?,
) : Exception(errorType?.name)

enum class TicketingErrorType {
    Unknown, NoRemainingQuantity, ApprovePaymentFailed;

    companion object {
        fun fromString(type: String?) = when (type?.trim()?.uppercase()) {
            "NO_REMAINING_QUANTITY" -> NoRemainingQuantity
            "APPROVE_PAYMENT_FAILED" -> ApprovePaymentFailed
            else -> Unknown
        }
    }
}
