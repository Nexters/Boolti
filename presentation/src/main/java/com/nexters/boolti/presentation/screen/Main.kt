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

    NavHost(
        navController = navController,
        startDestination = "home",
    ) {
        composable(
            route = "home",
        ) {
            HomeScreen(
                modifier = modifier,
            ) { screenName ->
                navController.navigate("login/$screenName")
            }
        }

        val previousScreen = "previousScreen"
        composable(
            route = "login/{$previousScreen}",
            arguments = listOf(navArgument(previousScreen) { type = NavType.StringType })
        ) { backstackEntry ->
            LoginScreen(
                modifier = modifier,
                previousScreen = backstackEntry.arguments?.getString(previousScreen) ?: "이전 화면",
            ) {
                navController.popBackStack()
            }
        }
    }
}
