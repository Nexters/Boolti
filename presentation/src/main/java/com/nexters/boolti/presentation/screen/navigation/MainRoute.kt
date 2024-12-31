package com.nexters.boolti.presentation.screen.navigation

import kotlinx.serialization.Serializable

sealed interface MainRoute {
    @Serializable
    data object Home : MainRoute

    @Serializable
    data class ShowDetail(
        val showId: String,
    ) : MainRoute

    @Serializable
    data class Ticketing(
        val showId: String,
        val salesTicketId: String,
        val ticketCount: Int,
        val isInviteTicket: Boolean,
    ) : MainRoute

    @Serializable
    data class Gift(
        val showId: String,
        val salesTicketId: String,
        val ticketCount: Int,
    ) : MainRoute

    @Serializable
    data class PaymentComplete(
        val reservationId: String,
        val showId: String,
    ) : MainRoute

    @Serializable
    data class GiftComplete(
        val giftId: String,
    ) : MainRoute

    @Serializable
    data object Reservations : MainRoute

    @Serializable
    data class ReservationDetail(
        val reservationId: String,
        val isGift: Boolean,
    ) : MainRoute

    @Serializable
    data class Refund(
        val reservationId: String,
        val isGift: Boolean
    ) : MainRoute

    @Serializable
    data object HostedShows : MainRoute

    @Serializable
    data object SignOut : MainRoute

    @Serializable
    data object Login : MainRoute

    @Serializable
    data object Business : MainRoute

    @Serializable
    data object AccountSetting : MainRoute

    @Serializable
    data class Profile(
        val userCode: String? = null
    ) : MainRoute

    @Serializable
    data class LinkList(
        val userCode: String? = null,
    ) : MainRoute

    @Serializable
    data class PerformedShows(
        val userCode: String? = null,
    ) : MainRoute
}
