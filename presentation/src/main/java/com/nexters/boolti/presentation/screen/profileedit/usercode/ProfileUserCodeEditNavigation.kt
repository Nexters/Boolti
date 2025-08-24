package com.nexters.boolti.presentation.screen.profileedit.usercode

import androidx.compose.ui.Modifier
import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import com.nexters.boolti.presentation.screen.LocalNavController
import com.nexters.boolti.presentation.screen.navigation.ProfileRoute

fun NavGraphBuilder.profileUserCodeEditScreen(
    modifier: Modifier = Modifier,
) {
    composable<ProfileRoute.ProfileUserCodeEdit> {
        val navController = LocalNavController.current
        UserCodeEditScreen(
            modifier = modifier,
            navigateUp = navController::popBackStack,
        )
    }
}
