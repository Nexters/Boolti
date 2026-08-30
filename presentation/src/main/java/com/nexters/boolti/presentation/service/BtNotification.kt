package com.nexters.boolti.presentation.service

import com.nexters.boolti.presentation.screen.navigation.HomeRoute

enum class BtNotification(val id: Int, val type: String, val homeRoute: HomeRoute?) {
    RESERVATION_COMPLETED(id = 0, type = "RESERVATION_COMPLETED", homeRoute = HomeRoute.Ticket),
    ENTER_NOTIFICATION(id = 3, type = "ENTER_NOTIFICATION", homeRoute = HomeRoute.Ticket),
    UNDEFINED(id = -1, type = "UNDEFINED", homeRoute = null),
}

fun BtNotification(type: String?): BtNotification {
    return when(type) {
        BtNotification.RESERVATION_COMPLETED.type -> BtNotification.RESERVATION_COMPLETED
        BtNotification.ENTER_NOTIFICATION.type -> BtNotification.ENTER_NOTIFICATION
        else -> BtNotification.UNDEFINED
    }
}
