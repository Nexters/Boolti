package com.nexters.boolti.presentation.extension

import android.content.Context
import androidx.compose.runtime.Composable
import androidx.compose.ui.res.stringResource
import com.nexters.boolti.domain.model.PaymentType
import com.nexters.boolti.domain.model.ReservationDetail
import com.nexters.boolti.presentation.R

@Composable
fun ReservationDetail.getPaymentString(context: Context): String {
    val cardName = cardDetail?.issuerCode?.cardCodeToCompanyName(context)
    val periodForPay = if (cardDetail?.installmentPlanMonths == 0) {
        stringResource(id = R.string.payment_pay_in_full)
    } else {
        stringResource(
            R.string.payment_installment_plan_months,
            cardDetail?.installmentPlanMonths ?: 0
        )
    }

    return when (paymentType) {
        PaymentType.ACCOUNT_TRANSFER -> stringResource(id = R.string.payment_account_transfer)
        PaymentType.CARD -> "$cardName / $periodForPay"
        PaymentType.SIMPLE_PAYMENT -> provider
        PaymentType.FREE -> stringResource(id = R.string.payment_free)
        else -> stringResource(id = R.string.reservations_unknown)
    }
}
