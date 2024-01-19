package com.nexters.boolti.presentation.screen

import androidx.annotation.StringRes
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.List
import androidx.compose.material.icons.filled.Person
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.nexters.boolti.presentation.R
import com.nexters.boolti.presentation.screen.home.HomeScreen
import com.nexters.boolti.presentation.screen.my.MyScreen
import com.nexters.boolti.presentation.screen.ticket.TicketScreen

sealed class BottomNavScreen(
    val route: String,
    @StringRes val title: Int,
    val icon: ImageVector,
) {
    data object Home : BottomNavScreen(route = "home", title = R.string.menu_home, icon = Icons.Default.Home)
    data object Tickets : BottomNavScreen(route = "tickets", title = R.string.menu_tickets, icon = Icons.Default.List)
    data object My : BottomNavScreen(route = "my", title = R.string.menu_my, icon = Icons.Default.Person)
}


@Composable
fun BottomNavGraph(navController: NavHostController, modifier: Modifier = Modifier) {
    NavHost(
        navController = navController,
        startDestination = BottomNavScreen.Home.route,
        modifier = modifier,
    ) {
        composable(route = BottomNavScreen.Home.route) {
            HomeScreen()
        }
        composable(route = BottomNavScreen.Tickets.route) {
            TicketScreen()
        }
        composable(route = BottomNavScreen.My.route) {
            MyScreen()
        }
    }
}
