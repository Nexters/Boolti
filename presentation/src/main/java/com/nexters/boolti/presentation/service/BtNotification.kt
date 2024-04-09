package com.nexters.boolti.presentation.service

enum class BtNotification(val id: Int, val type: String, val deepLink: String?) {
    RESERVATION_COMPLETED(id = 0, type = "RESERVATION_COMPLETED", deepLink = "https://app.boolti.in/home/tickets"),
    ENTER_NOTIFICATION(id = 3, type = "ENTER_NOTIFICATION", deepLink = "https://app.boolti.in/home/tickets"),
    UNDEFINED(id = -1, type = "UNDEFINED", deepLink = null),
}

fun BtNotification(type: String?): BtNotification {
    return when(type) {
        BtNotification.RESERVATION_COMPLETED.type -> BtNotification.RESERVATION_COMPLETED
        BtNotification.ENTER_NOTIFICATION.type -> BtNotification.ENTER_NOTIFICATION
        else -> BtNotification.UNDEFINED
    }
}
