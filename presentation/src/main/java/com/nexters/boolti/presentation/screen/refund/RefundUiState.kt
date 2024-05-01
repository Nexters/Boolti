package com.nexters.boolti.presentation.screen.refund

import com.nexters.boolti.domain.model.ReservationDetail

data class RefundUiState(
    val reason: String = "",
    val reservation: ReservationDetail? = null,
    val refundPolicyChecked: Boolean = false
) {
    val isAbleToRequest: Boolean get() = reason.isNotBlank() && refundPolicyChecked
}
