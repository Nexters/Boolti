package com.nexters.boolti.presentation.screen.gift

import androidx.compose.runtime.Stable
import com.nexters.boolti.domain.model.ImagePair
import com.nexters.boolti.presentation.R
import java.time.LocalDateTime

@Stable
data class GiftUiState(
    val loading: Boolean = true,
    val giftImages: List<ImagePair> = emptyList(),
    val poster: String = "",
    val showDate: LocalDateTime = LocalDateTime.now(),
    val showName: String = "",
    val ticketName: String = "",
    val ticketCount: Int = 1,
    val totalPrice: Int = 0,
    val refundPolicy: List<String> = emptyList(),
    val orderAgreement: List<Pair<Int, Boolean>> = listOf(
        Pair(R.string.order_agreement_privacy_collection, false),
        Pair(R.string.order_agreement_privacy_offer, false),
    ),
    val message: String = "공연에 초대합니다.",
    val selectedImage: ImagePair? = null,
    val senderName: String = "",
    val senderContact: String = "",
    val receiverName: String = "",
    val receiverContact: String = "",
) {
    val orderAgreed: Boolean
        get() = orderAgreement.none { !it.second }

    val isComplete: Boolean
        get() = orderAgreed && senderName.isNotBlank() && senderContact.isNotBlank() &&
                receiverName.isNotBlank() && receiverContact.isNotBlank()

    fun toggleAgreement(): GiftUiState =
        copy(orderAgreement = orderAgreement.map { it.copy(second = !orderAgreed) })
}
