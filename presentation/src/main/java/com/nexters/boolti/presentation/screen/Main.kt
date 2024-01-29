package com.nexters.boolti.presentation.screen

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material.Surface
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.nexters.boolti.presentation.screen.home.HomeScreen
import com.nexters.boolti.presentation.screen.login.LoginScreen
import com.nexters.boolti.presentation.screen.show.ShowDetailScreen
import com.nexters.boolti.presentation.screen.ticket.TicketDetailScreen
import com.nexters.boolti.presentation.screen.ticketing.TicketingScreen
import com.nexters.boolti.presentation.theme.BooltiTheme

@Composable
fun Main() {
    val modifier = Modifier.fillMaxSize()
    BooltiTheme {
        Surface(modifier) {
            MainNavigation(modifier)
        }
    }
}

@Composable
fun MainNavigation(modifier: Modifier, viewModel: MainViewModel = hiltViewModel()) {
    val navController = rememberNavController()

    // TODO: 하드코딩 된 route 를 각 화면에 정의
    NavHost(
        navController = navController,
        startDestination = "home",
    ) {
        composable(
            route = "home",
        ) {
            HomeScreen(
                modifier = modifier,
                onClickShowItem = {
                    navController.navigate("show/$it")
                },
                onClickTicket = {
                    navController.navigate("ticket/$it")
                },
            ) {
                navController.navigate("login")
            }
        }

        composable(
            route = "login",
        ) {
            LoginScreen(
                modifier = modifier,
            ) {
                navController.popBackStack()
            }
        }
        composable(
            route = "show/{showId}",
            arguments = listOf(navArgument("showId") { type = NavType.StringType }),
        ) {
            ShowDetailScreen(modifier = modifier) {
                navController.navigate("ticketing/$it")
            }
        }
        composable(
            route = "ticket/{ticketId}",
            arguments = listOf(navArgument("ticketId") { type = NavType.StringType }),
        ) {
            TicketDetailScreen(modifier = modifier)
        }
        composable(
            route = "ticketing/{showId}",
            arguments = listOf(navArgument("showId") { type = NavType.StringType }),
        ) {
            TicketingScreen(modifier = modifier) {
                navController.popBackStack()
            }
        }
    }
}
