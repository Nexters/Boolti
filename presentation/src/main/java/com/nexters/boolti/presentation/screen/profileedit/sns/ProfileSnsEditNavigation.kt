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
            onAddSns = { type, username ->
                navController.previousBackStackEntry
                    ?.savedStateHandle
                    ?.apply {
                        set("newSnsType", type)
                        set("newSnsUsername", username)
                    }
                navController.popBackStack()
            },
            onEditSns = { id, type, username ->
                navController.previousBackStackEntry
                    ?.savedStateHandle
                    ?.apply {
                        set("editSnsId", id)
                        set("editSnsType", type)
                        set("editSnsUsername", username)
                    }
                navController.popBackStack()
            },
            onRemoveSns = { id ->
                navController.previousBackStackEntry
                    ?.savedStateHandle
                    ?.apply {
                        set("removeSnsId", id)
                    }
                navController.popBackStack()
            },
            navigateBack = navController::popBackStack,
        )
    }
}
