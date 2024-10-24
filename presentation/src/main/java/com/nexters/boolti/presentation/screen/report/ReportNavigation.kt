package com.nexters.boolti.presentation.screen.report

import androidx.compose.ui.Modifier
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavHostController
import androidx.navigation.compose.composable

fun NavGraphBuilder.reportScreen(
    navController: NavHostController,
    navigateToHome: () -> Unit,
    popBackStack: () -> Unit,
    modifier: Modifier = Modifier,
) {
    composable(
        route = "report/{showId}",
    ) {
        ReportScreen(
            onBackPressed = popBackStack,
            popupToHome = navigateToHome,
            modifier = modifier,
        )
    }
}
