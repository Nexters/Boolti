package com.nexters.boolti.presentation.screen.gift

import androidx.compose.runtime.Stable
import com.nexters.boolti.presentation.R

@Stable
data class GiftUiState(
    val message: String = "",
    val orderAgreement: List<Pair<Int, Boolean>> = listOf(
        Pair(R.string.order_agreement_privacy_collection, false),
        Pair(R.string.order_agreement_privacy_offer, false),
    )
) {
    val orderAgreed: Boolean
        get() = orderAgreement.none { !it.second }

    fun toggleAgreement(): GiftUiState =
        copy(orderAgreement = orderAgreement.map { it.copy(second = !orderAgreed) })
}
