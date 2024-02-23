package com.nexters.boolti.presentation.screen.home

import androidx.annotation.DrawableRes
import androidx.annotation.StringRes
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Divider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.Stable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.nexters.boolti.presentation.R
import com.nexters.boolti.presentation.screen.HomeViewModel
import com.nexters.boolti.presentation.screen.my.MyScreen
import com.nexters.boolti.presentation.screen.show.ShowScreen
import com.nexters.boolti.presentation.screen.ticket.TicketLoginScreen
import com.nexters.boolti.presentation.screen.ticket.TicketScreen
import com.nexters.boolti.presentation.theme.Grey10
import com.nexters.boolti.presentation.theme.Grey50
import com.nexters.boolti.presentation.theme.Grey85

@Composable
fun HomeScreen(
    modifier: Modifier,
    viewModel: HomeViewModel = hiltViewModel(),
    onClickShowItem: (showId: String) -> Unit,
    onClickTicket: (ticketId: String) -> Unit,
    onClickQr: (data: String, ticketName: String) -> Unit,
    onClickQrScan: () -> Unit,
    onClickSignout: () -> Unit,
    navigateToReservations: () -> Unit,
    requireLogin: () -> Unit,
) {
    val navController = rememberNavController()
    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentDestination = navBackStackEntry?.destination?.route ?: Destination.Show.route

    val loggedIn by viewModel.loggedIn.collectAsStateWithLifecycle()

    Scaffold(
        bottomBar = {
            HomeNavigationBar(
                currentDestination = currentDestination,
                onDestinationChanged = {
                    navController.navigate(it.route) {
                        popUpTo(navController.graph.findStartDestination().id)
                        launchSingleTop = true
                        restoreState = true
                    }
                },
            )
        }
    ) { innerPadding ->
        NavHost(
            navController = navController,
            startDestination = Destination.Show.route,
            modifier = modifier,
        ) {
            composable(
                route = Destination.Show.route,
            ) {
                ShowScreen(
                    modifier = modifier.padding(innerPadding),
                    onClickShowItem = onClickShowItem,
                    navigateToReservations = navigateToReservations,
                )
            }
            composable(
                route = Destination.Ticket.route,
            ) {
                when (loggedIn) {
                    true -> TicketScreen(
                        onClickTicket = onClickTicket,
                        onClickQr = onClickQr,
                        modifier = modifier.padding(innerPadding),
                    )

                    false -> TicketLoginScreen(modifier.padding(innerPadding), onLoginClick = requireLogin)
                    else -> Unit // 로그인 여부를 불러오는 중
                }
            }
            composable(
                route = Destination.My.route,
            ) {
                MyScreen(
                    modifier = modifier.padding(innerPadding),
                    requireLogin = requireLogin,
                    navigateToReservations = navigateToReservations,
                    onClickQrScan = onClickQrScan,
                    onClickSignout = onClickSignout,
                )
            }
        }
    }
}

@Stable
private enum class Destination(
    val route: String,
    @StringRes val label: Int,
    @DrawableRes val icon: Int,
) {
    Show(route = "show", label = R.string.menu_show, icon = R.drawable.ic_home),
    Ticket(route = "tickets", label = R.string.menu_tickets, R.drawable.ic_ticket),
    My(route = "my", label = R.string.menu_my, icon = R.drawable.ic_person)
}

@Composable
private fun HomeNavigationBar(
    currentDestination: String,
    onDestinationChanged: (Destination) -> Unit,
    modifier: Modifier = Modifier,
) {
    Column {
        Divider(
            modifier = Modifier.fillMaxWidth(),
            thickness = 1.dp,
            color = Grey85,
        )
        NavigationBar(
            modifier = modifier,
            containerColor = MaterialTheme.colorScheme.background,
        ) {
            Destination.entries.forEach { dest ->
                val selected = currentDestination == dest.route
                val label = stringResource(dest.label)
                NavigationBarItem(
                    selected = selected,
                    onClick = { onDestinationChanged(dest) },
                    icon = {
                        Icon(
                            painter = painterResource(dest.icon),
                            contentDescription = label,
                        )
                    },
                    label = {
                        Text(label, style = MaterialTheme.typography.labelMedium)
                    },
                    colors = NavigationBarItemDefaults.colors(
                        selectedIconColor = Grey10,
                        selectedTextColor = Grey10,
                        unselectedIconColor = Grey50,
                        unselectedTextColor = Grey50,
                        indicatorColor = MaterialTheme.colorScheme.background,
                    ),
                )
            }
        }
    }
}
