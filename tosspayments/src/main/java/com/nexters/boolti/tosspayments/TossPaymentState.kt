package com.nexters.boolti.tosspayments

import androidx.lifecycle.SavedStateHandle

internal sealed class TossPaymentState(
    open val showId: String,
    open val salesTicketTypeId: String,
    open val ticketCount: Int,
) {
    data class Ticketing(
        override val showId: String,
        override val salesTicketTypeId: String,
        override val ticketCount: Int,
        val reservationName: String,
        val reservationPhoneNumber: String,
        val depositorName: String,
        val depositorPhoneNumber: String,
    ) : TossPaymentState(showId, salesTicketTypeId, ticketCount)

    data class Gift(
        override val showId: String,
        override val salesTicketTypeId: String,
        override val ticketCount: Int,
        val senderName: String,
        val senderContact: String,
        val receiverName: String,
        val receiverContact: String,
        val giftMessage: String,
        val giftImageId: String,
    ) : TossPaymentState(showId, salesTicketTypeId, ticketCount)

    companion object {
        fun from(savedStateHandle: SavedStateHandle): TossPaymentState {
            val orderType: OrderType = savedStateHandle[EXTRA_KEY_ORDER_TYPE] ?: OrderType.TICKETING
            val showId: String = savedStateHandle[EXTRA_KEY_SHOW_ID] ?: ""
            val salesTicketTypeId: String = savedStateHandle[EXTRA_KEY_SALES_TICKET_ID] ?: ""
            val ticketCount: Int = savedStateHandle[EXTRA_KEY_TICKET_COUNT] ?: 1

            return if (orderType == OrderType.TICKETING) {
                val reservationName: String = savedStateHandle[EXTRA_KEY_RESERVATION_NAME] ?: ""
                val reservationPhoneNumber: String =
                    savedStateHandle[EXTRA_KEY_RESERVATION_PHONE_NUMBER] ?: ""
                val depositorName: String = savedStateHandle[EXTRA_KEY_DEPOSITOR_NAME] ?: ""
                val depositorPhoneNumber: String =
                    savedStateHandle[EXTRA_KEY_DEPOSITOR_PHONE_NUMBER] ?: ""
                Ticketing(
                    showId,
                    salesTicketTypeId,
                    ticketCount,
                    reservationName,
                    reservationPhoneNumber,
                    depositorName,
                    depositorPhoneNumber
                )
            } else {
                val senderName: String = savedStateHandle[EXTRA_KEY_SENDER_NAME] ?: ""
                val senderContact: String = savedStateHandle[EXTRA_KEY_SENDER_PHONE_NUMBER] ?: ""
                val receiverName: String = savedStateHandle[EXTRA_KEY_RECEIVER_NAME] ?: ""
                val receiverContact: String =
                    savedStateHandle[EXTRA_KEY_RECEIVER_PHONE_NUMBER] ?: ""
                val giftMessage: String = savedStateHandle[EXTRA_KEY_MESSAGE] ?: ""
                val giftImageId: String = savedStateHandle[EXTRA_KEY_IMAGE_ID] ?: ""
                Gift(
                    showId,
                    salesTicketTypeId,
                    ticketCount,
                    senderName,
                    senderContact,
                    receiverName,
                    receiverContact,
                    giftMessage,
                    giftImageId
                )
            }
        }
    }
}