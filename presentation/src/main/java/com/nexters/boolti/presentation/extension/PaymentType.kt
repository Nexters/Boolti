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
    return when (paymentType) {
        PaymentType.ACCOUNT_TRANSFER -> stringResource(id = R.string.payment_account_transfer)
        PaymentType.CARD -> "$cardName / ${stringResource(id = R.string.payment_pay_in_full)}"
        PaymentType.SIMPLE_PAYMENT -> provider
        else -> stringResource(id = R.string.reservations_unknown)
    }
}
