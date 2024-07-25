package com.nexters.boolti.domain.model

import java.time.LocalDateTime

data class ReservationDetail(
    val id: String,
    val showImage: String,
    val showName: String,
    val showDate: LocalDateTime,
    val giftUuid: String? = null,
    val giftInviteImage: String = "",
    val ticketName: String,
    val isInviteTicket: Boolean,
    val ticketCount: Int,
    val bankName: String,
    val accountNumber: String,
    val accountHolder: String,
    val salesEndDateTime: LocalDateTime,
    val paymentType: PaymentType,
    val totalAmountPrice: Int,
    val reservationState: ReservationState,
    val completedDateTime: LocalDateTime?,
    val visitorName: String,
    val visitorPhoneNumber: String,
    val depositorName: String,
    val depositorPhoneNumber: String,
    val csReservationId: String,
    val cardDetail: CardDetail?,
    val provider: String = "",
) {
    /**
     * @param installmentPlanMonths 할부 개월 수. 0 이면 일시불
     * @param issuerCode 카드 발급사 [숫자 코드](https://docs.tosspayments.com/reference/codes#%EC%B9%B4%EB%93%9C%EC%82%AC-%EC%BD%94%EB%93%9C)
     */
    data class CardDetail(
        val installmentPlanMonths: Int,
        val issuerCode: String,
    )
}
