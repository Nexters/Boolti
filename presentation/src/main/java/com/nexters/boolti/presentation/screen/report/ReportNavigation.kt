package com.nexters.boolti.presentation.screen.report

import androidx.compose.ui.Modifier
import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import com.nexters.boolti.presentation.extension.navigateToHome
import com.nexters.boolti.presentation.screen.LocalNavController
import com.nexters.boolti.presentation.screen.navigation.ShowRoute

fun NavGraphBuilder.reportScreen(
    modifier: Modifier = Modifier,
) {
    composable<ShowRoute.Report> {
        val navController = LocalNavController.current
        ReportScreen(
            onBackPressed = navController::popBackStack,
            popupToHome = navController::navigateToHome,
            modifier = modifier,
        )
    }
}
