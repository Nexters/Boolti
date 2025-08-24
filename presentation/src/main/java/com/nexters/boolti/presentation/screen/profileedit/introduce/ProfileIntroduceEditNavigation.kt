package com.nexters.boolti.presentation.screen.profileedit.introduce

import androidx.compose.ui.Modifier
import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import com.nexters.boolti.presentation.screen.LocalNavController
import com.nexters.boolti.presentation.screen.navigation.ProfileRoute

fun NavGraphBuilder.profileIntroduceEditScreen(
    modifier: Modifier = Modifier,
) {
    composable<ProfileRoute.ProfileIntroduceEdit> {
        val navController = LocalNavController.current
        IntroduceEditScreen(
            modifier = modifier,
            navigateUp = navController::popBackStack,
        )
    }
}
