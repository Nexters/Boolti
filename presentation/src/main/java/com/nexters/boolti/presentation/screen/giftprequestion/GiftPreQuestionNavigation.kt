package com.nexters.boolti.presentation.screen.giftprequestion

import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import androidx.navigation.toRoute
import com.nexters.boolti.presentation.screen.LocalNavController
import com.nexters.boolti.presentation.screen.navigation.MainRoute

fun NavGraphBuilder.giftPreQuestionScreen() {
    composable<MainRoute.GiftPreQuestion> { backStackEntry ->
        val navController = LocalNavController.current

        GiftPreQuestionScreen(
            onBackPressed = navController::navigateUp,
        )
    }
}