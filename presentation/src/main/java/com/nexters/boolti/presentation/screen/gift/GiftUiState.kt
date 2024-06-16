package com.nexters.boolti.presentation.screen.gift

import androidx.compose.runtime.Stable
import com.nexters.boolti.presentation.R
import java.time.LocalDateTime

@Stable
data class GiftUiState(
    val message: String = "",
    val poster: String = "",
    val showDate: LocalDateTime = LocalDateTime.now(),
    val showName: String = "",
    val ticketName: String = "", // TODO : 티켓 정보 업데이트하기
    val ticketCount: Int = 1,
    val totalPrice: Int = 0,
    val refundPolicy: List<String> = emptyList(),
    val orderAgreement: List<Pair<Int, Boolean>> = listOf(
        Pair(R.string.order_agreement_privacy_collection, false),
        Pair(R.string.order_agreement_privacy_offer, false),
    ),
) {
    val orderAgreed: Boolean
        get() = orderAgreement.none { !it.second }

    fun toggleAgreement(): GiftUiState =
        copy(orderAgreement = orderAgreement.map { it.copy(second = !orderAgreed) })
}
