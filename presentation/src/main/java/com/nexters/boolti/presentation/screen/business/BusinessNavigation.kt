package com.nexters.boolti.presentation.screen.business

import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavHostController
import androidx.navigation.compose.composable
import com.nexters.boolti.presentation.screen.navigation.MainRoute

fun NavGraphBuilder.businessScreen(
    navController: NavHostController,
    popBackStack: () -> Unit,
) {
    composable<MainRoute.Business> {
        BusinessScreen(
            onBackPressed = popBackStack
        )
    }
}