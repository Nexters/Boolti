package com.nexters.boolti.presentation.screen.profileedit.nickname

import androidx.compose.ui.Modifier
import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import com.nexters.boolti.presentation.screen.LocalNavController
import com.nexters.boolti.presentation.screen.navigation.ProfileRoute

fun NavGraphBuilder.profileNicknameEditScreen(
    modifier: Modifier = Modifier,
) {
    composable<ProfileRoute.ProfileNicknameEdit> {
        val navController = LocalNavController.current
        NicknameEditScreen(
            modifier = modifier,
            navigateUp = navController::popBackStack,
        )
    }
}
