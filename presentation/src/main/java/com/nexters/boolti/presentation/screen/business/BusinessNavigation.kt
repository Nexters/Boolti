package com.nexters.boolti.presentation.screen.business

import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavHostController
import androidx.navigation.compose.composable
import com.nexters.boolti.presentation.screen.MainDestination

fun NavGraphBuilder.businessScreen(
    navController: NavHostController,
    popBackStack: () -> Unit,
) {
    composable(
        route = MainDestination.Business.route,
    ) {
        BusinessScreen(
            onBackPressed = popBackStack
        )
    }
}