package com.nexters.boolti.presentation.screen.refund

import androidx.compose.runtime.Stable
import com.nexters.boolti.domain.model.ReservationDetail

@Stable
data class RefundUiState(
    val reason: String = "",
    val reservation: ReservationDetail? = null,
    val refundPolicyChecked: Boolean = false
) {
    val isAbleToRequest = refundPolicyChecked
}
