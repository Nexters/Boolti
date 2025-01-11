package com.nexters.boolti.presentation.screen.navigation

import com.nexters.boolti.presentation.R
import kotlinx.serialization.Serializable

sealed interface HomeRoute {
    val label: Int
    val icon: Int

    @Serializable
    data object Show : HomeRoute {
        override val label = R.string.menu_show
        override val icon = R.drawable.ic_home
    }

    @Serializable
    data object Ticket : HomeRoute {
        override val label = R.string.menu_tickets
        override val icon = R.drawable.ic_ticket
    }

    @Serializable
    data object My : HomeRoute {
        override val label = R.string.menu_my
        override val icon: Int = R.drawable.ic_person
    }
}

val homeRoutes = listOf(HomeRoute.Show, HomeRoute.Ticket, HomeRoute.My)