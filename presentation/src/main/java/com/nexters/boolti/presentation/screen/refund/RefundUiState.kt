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
    val isValidName: Boolean get() {
        val regex = "^[가-힣]{2,10}$".toRegex()
        return regex.matches(name)
    }

    val isValidContact: Boolean get() {
        val regex = "^0[0-9]{8,10}$".toRegex()
        return regex.matches(contact)
    }

    val isValidAccountNumber: Boolean get() {
        val regex = "^[0-9]{11,14}$".toRegex()
        return regex.matches(accountNumber)
    }

    val isAbleToRequest: Boolean get() {
        return reason.isNotBlank() && isValidName && isValidContact && (bankInfo != null) && isValidAccountNumber
    }
}