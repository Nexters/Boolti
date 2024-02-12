package com.nexters.boolti.domain.model

import java.time.LocalDateTime

data class TicketingInfo(
    val saleTicketName: String = "",
    val showDate: LocalDateTime = LocalDateTime.now(),
    val showImg: String = "",
    val showName: String = "",
    val ticketCount: Int = 0,
    val totalPrice: Int = 0,
    val isInviteTicket: Boolean = false,
    val paymentType: PaymentType = PaymentType.AccountTransfer,
)

enum class PaymentType {
    Card, AccountTransfer
}
