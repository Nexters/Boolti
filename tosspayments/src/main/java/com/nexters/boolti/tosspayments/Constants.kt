package com.nexters.boolti.tosspayments

internal enum class OrderType {
    TICKETING,
    GIFT,
}

// 공통
internal const val EXTRA_KEY_ORDER_TYPE = "extraKeyOrderType"
internal const val EXTRA_KEY_AMOUNT = "extraKeyAmount"
internal const val EXTRA_KEY_CLIENT_KEY = "extraKeyClientKey"
internal const val EXTRA_KEY_CUSTOMER_KEY = "extraKeyCustomerKey"
internal const val EXTRA_KEY_ORDER_ID = "extraKeyOrderId"
internal const val EXTRA_KEY_ORDER_NAME = "extraKeyOrderName"
internal const val EXTRA_KEY_CURRENCY = "extraKeyCurrency"
internal const val EXTRA_KEY_COUNTRY_CODE = "extraKeyCountryCode"
internal const val EXTRA_KEY_VARIANT_KEY = "extraKeyVariantKey"
internal const val EXTRA_KEY_REDIRECT_URL = "extraKeyRedirectUrl"
internal const val EXTRA_KEY_SHOW_ID = "extraKeyShowId"
internal const val EXTRA_KEY_SALES_TICKET_ID = "extraKeySalesTicketId"
internal const val EXTRA_KEY_TICKET_COUNT = "extraKeyTicketCount"

// 예매하기
internal const val EXTRA_KEY_RESERVATION_NAME = "extraKeyReservationName"
internal const val EXTRA_KEY_RESERVATION_PHONE_NUMBER = "extraKeyReservationPhoneNumber"
internal const val EXTRA_KEY_DEPOSITOR_NAME = "extraKeyDepositorName"
internal const val EXTRA_KEY_DEPOSITOR_PHONE_NUMBER = "extraKeyDepositorPhoneNumber"

// 선물하기
internal const val EXTRA_KEY_SENDER_NAME = "extraKeySenderName"
internal const val EXTRA_KEY_SENDER_PHONE_NUMBER = "extraKeySenderPhoneNumber"
internal const val EXTRA_KEY_RECEIVER_NAME = "extraKeyReceiverName"
internal const val EXTRA_KEY_RECEIVER_PHONE_NUMBER = "extraKeyReceiverPhoneNumber"
internal const val EXTRA_KEY_MESSAGE = "extraKeyMessage"
internal const val EXTRA_KEY_IMAGE_ID = "extraKeyImageId"