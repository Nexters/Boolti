package com.nexters.boolti.presentation.screen.login

import androidx.compose.ui.Modifier
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavHostController
import androidx.navigation.compose.composable
import com.nexters.boolti.presentation.screen.MainDestination

fun NavGraphBuilder.loginScreen(
    navController: NavHostController,
    popBackStack: () -> Unit,
    modifier: Modifier = Modifier,
) {
    composable(
        route = MainDestination.Login.route,
    ) {
        LoginScreen(
            modifier = modifier,
            onBackPressed = popBackStack
        )
    }
}
