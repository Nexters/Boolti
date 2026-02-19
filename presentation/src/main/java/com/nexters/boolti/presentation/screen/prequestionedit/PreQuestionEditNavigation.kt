package com.nexters.boolti.presentation.screen.prequestionedit

import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import com.nexters.boolti.presentation.screen.LocalNavController
import com.nexters.boolti.presentation.screen.navigation.MainRoute

fun NavGraphBuilder.preQuestionEditScreen() {
    composable<MainRoute.PreQuestionEdit> {
        val navController = LocalNavController.current
        PreQuestionEditScreen(
            onBackPressed = navController::navigateUp,
        )
    }
}
