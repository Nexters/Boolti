package com.nexters.boolti.presentation.screen.refund

import com.nexters.boolti.domain.model.ReservationDetail

data class RefundUiState(
    val name: String = "",
    val phoneNumber: String = "",
    val bankInfo: BankInfo? = null,
    val accountNumber: String = "",
    val reservation: ReservationDetail? = null,
)