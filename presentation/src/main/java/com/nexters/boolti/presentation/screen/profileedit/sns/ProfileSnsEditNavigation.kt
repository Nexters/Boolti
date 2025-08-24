package com.nexters.boolti.presentation.screen.profileedit.sns

import androidx.compose.ui.Modifier
import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import com.nexters.boolti.presentation.screen.LocalNavController
import com.nexters.boolti.presentation.screen.navigation.ProfileRoute

fun NavGraphBuilder.profileSnsEditScreen(
    modifier: Modifier = Modifier,
) {
    composable<ProfileRoute.ProfileSnsEdit> {
        val navController = LocalNavController.current
        SnsEditScreen(
            modifier = modifier,
            navigateUp = navController::popBackStack,
        )
    }
}
