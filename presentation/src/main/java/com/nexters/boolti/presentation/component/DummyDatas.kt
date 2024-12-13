package com.nexters.boolti.presentation.component

import com.nexters.boolti.domain.model.PaymentType
import com.nexters.boolti.domain.model.ReservationDetail
import com.nexters.boolti.domain.model.ReservationDetail.CardDetail
import com.nexters.boolti.domain.model.ReservationState
import java.time.LocalDateTime

/**
 * Preview를 위한 더미 데이터들
 */

val dummyCardDetail = CardDetail(
    installmentPlanMonths = 0,
    issuerCode = "",
)

val dummyReservationDetail = ReservationDetail(
    id = "12",
    showImage = "",
    showName = "Netflix 너무 비싸",
    showDate = LocalDateTime.MIN,
    giftUuid = "ab12-cd34",
    giftInviteImage = "",
    ticketName = "인섬니아",
    isInviteTicket = false,
    ticketCount = 1,
    bankName = "카카오 뱅크 가고 싶다",
    accountNumber = "352-2345-3456-13",
    accountHolder = "공주영",
    salesEndDateTime = LocalDateTime.MIN,
    paymentType = PaymentType.CARD,
    totalAmountPrice = 7000,
    reservationState = ReservationState.RESERVED,
    completedDateTime = LocalDateTime.MIN,
    visitorName = "왕자림",
    visitorPhoneNumber = "010-2048-1024",
    depositorName = "공주영",
    depositorPhoneNumber = "010-2048-4096",
    csReservationId = "cs-65535",
    cardDetail = dummyCardDetail,
    provider = ""
)
