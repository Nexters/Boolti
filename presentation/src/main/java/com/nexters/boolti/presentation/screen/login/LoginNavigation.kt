package com.nexters.boolti.presentation.screen.login

import androidx.compose.ui.Modifier
import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import com.nexters.boolti.presentation.screen.LocalNavController
import com.nexters.boolti.presentation.screen.navigation.MainRoute

fun NavGraphBuilder.loginScreen(
    modifier: Modifier = Modifier,
) {
    composable<MainRoute.Login> {
        val navController = LocalNavController.current
        LoginScreen(
            modifier = modifier,
            onBackPressed = navController::popBackStack
        )
    }
}
