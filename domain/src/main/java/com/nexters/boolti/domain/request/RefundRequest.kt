package com.nexters.boolti.domain.request

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class RefundRequest(
    val reservationId: String,
    @SerialName("refundReason") val reason: String,
    @SerialName("refundPhoneNumber") val phoneNumber: String,
    @SerialName("refundAccountName") val accountName: String,
    @SerialName("refundAccountNumber") val accountNumber: String,
    @SerialName("refundBankCode") val bankCode: String,
)
