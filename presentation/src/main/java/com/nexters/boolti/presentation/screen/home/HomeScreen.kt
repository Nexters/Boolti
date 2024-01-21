package com.nexters.boolti.presentation.screen.home

import androidx.annotation.StringRes
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.List
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.stringResource
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import com.nexters.boolti.presentation.R
import com.nexters.boolti.presentation.screen.MainViewModel
import com.nexters.boolti.presentation.screen.my.MyScreen
import com.nexters.boolti.presentation.screen.show.ShowScreen
import com.nexters.boolti.presentation.screen.ticket.TicketScreen

@Composable
fun HomeScreen(
    navController: NavHostController,
    modifier: Modifier,
    viewModel: MainViewModel = hiltViewModel(),
    requireLogin: () -> Unit,
) {
    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentDestination = navBackStackEntry?.destination?.route ?: Destination.Show.route
    val loggedIn by viewModel.loggedIn.collectAsState()
    val shouldLogin = currentDestination in listOf(Destination.Ticket.route, Destination.My.route)

    if (loggedIn == false && shouldLogin) {
        requireLogin()
    }

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
                    modifier = modifier,
                )
            }
            composable(
                route = Destination.Ticket.route,
            ) {
                TicketScreen(
                    modifier = modifier.padding(innerPadding),
                )
            }
            composable(
                route = Destination.My.route,
            ) {
                MyScreen(
                    modifier = modifier.padding(innerPadding),
                )
            }
        }
    }
}

private enum class Destination(
    val route: String,
    @StringRes val label: Int,
    val icon: ImageVector,
) {
    Show(route = "show", label = R.string.menu_show, icon = Icons.Default.Home),
    Ticket(route = "ticket", label = R.string.menu_tickets, icon = Icons.Default.List),
    My(route = "my", label = R.string.menu_my, icon = Icons.Default.Person)
}

@Composable
private fun HomeNavigationBar(
    currentDestination: String,
    onDestinationChanged: (Destination) -> Unit,
    modifier: Modifier = Modifier,
) {
    NavigationBar(modifier) {
        Destination.entries.forEach { dest ->
            val selected = currentDestination == dest.route
            val label = stringResource(dest.label)
            NavigationBarItem(
                selected = selected,
                onClick = { onDestinationChanged(dest) },
                icon = {
                    Icon(
                        imageVector = dest.icon,
                        contentDescription = label,
                    )
                },
                label = {
                    Text(label)
                }
            )
        }
    }
}
