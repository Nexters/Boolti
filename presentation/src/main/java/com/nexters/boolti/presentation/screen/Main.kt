package com.nexters.boolti.presentation.screen

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.nexters.boolti.presentation.screen.home.HomeScreen
import com.nexters.boolti.presentation.screen.login.LoginScreen
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
    val homeNavController = rememberNavController()
    val loggedIn by viewModel.loggedIn.collectAsState()

    NavHost(
        navController = navController,
        startDestination = "home",
    ) {
        composable(
            route = "home",
        ) {
            HomeScreen(
                navController = homeNavController,
                modifier = modifier,
            ) {
                navController.navigate("login") {
                    popUpTo("home")
                    launchSingleTop = true
                    restoreState = true
                }
            }
        }
        composable(
            route = "login"
        ) {
            if (loggedIn == true) navController.popBackStack("home", false)
            if (loggedIn == false) LoginScreen(modifier = modifier) {
                navController.popBackStack()
                homeNavController.navigateToHome()
            }
        }
    }
}

fun NavHostController.navigateToHome() {
    val homeRoute = "show" // HomeScreen 의 StartDestination.route 와 일치해야 함
    navigate(homeRoute) {
        popUpTo(homeRoute)
        launchSingleTop = true
    }
}