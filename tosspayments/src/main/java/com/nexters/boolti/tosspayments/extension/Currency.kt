package com.nexters.boolti.tosspayments.extension

import com.tosspayments.paymentsdk.view.PaymentMethod

fun String.toCurrency(): PaymentMethod.Rendering.Currency = when (this) {
    "KRW" -> PaymentMethod.Rendering.Currency.KRW
    "AUD" -> PaymentMethod.Rendering.Currency.AUD
    "EUR" -> PaymentMethod.Rendering.Currency.EUR
    "GBP" -> PaymentMethod.Rendering.Currency.GBP
    "HKD" -> PaymentMethod.Rendering.Currency.HKD
    "JPY" -> PaymentMethod.Rendering.Currency.JPY
    "SGD" -> PaymentMethod.Rendering.Currency.SGD
    "USD" -> PaymentMethod.Rendering.Currency.USD
    else -> throw IllegalArgumentException("Unknown currency: $this")
}
