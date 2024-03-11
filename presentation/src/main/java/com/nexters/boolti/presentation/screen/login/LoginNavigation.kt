package com.nexters.boolti.presentation.screen.login

import androidx.compose.ui.Modifier
import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import com.nexters.boolti.presentation.screen.MainDestination

fun NavGraphBuilder.LoginScreen(
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
