package com.nexters.boolti.presentation.screen.refund

import com.nexters.boolti.domain.model.ReservationDetail

data class RefundUiState(
    val reason: String = "",
    val name: String = "",
    val contact: String = "",
    val bankInfo: BankInfo? = null,
    val accountNumber: String = "",
    val reservation: ReservationDetail? = null,
) {
    val isValidName: Boolean get() = name.isNotBlank()

    val isValidContact: Boolean get() = contact.isNotBlank()

    val isValidAccountNumber: Boolean get() {
        val regex = "^[0-9]{11,14}$".toRegex()
        return regex.matches(accountNumber)
    }

    val isAbleToRequest: Boolean get() {
        return reason.isNotBlank() && isValidName && isValidContact && (bankInfo != null) && isValidAccountNumber
    }
}