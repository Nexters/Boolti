package com.nexters.boolti.presentation.screen.navigation.deeplink

private const val PATH_BASE = "https://app.boolti.in"
private const val TAG_HOME = "home"
private const val TAG_TICKETS = "tickets"

/** https://app.boolti.in/home */
internal const val PATH_BASE_HOME = "$PATH_BASE/$TAG_HOME"

/** https://app.boolti.in/home/shows */
internal const val DEEPLINK_SHOWS = "$PATH_BASE_HOME/shows"

/** https://app.boolti.in/home/search */
internal const val DEEPLINK_SEARCH = "$PATH_BASE_HOME/search"

/** https://app.boolti.in/home/tickets */
internal const val DEEPLINK_TICKETS = "$PATH_BASE_HOME/tickets"

/** https://app.boolti.in/tickets */
internal const val PATH_BASE_TICKETS = "$PATH_BASE/$TAG_TICKETS"

internal fun deepLinkTicket(ticketId: String): String = "$PATH_BASE_TICKETS/$ticketId"
