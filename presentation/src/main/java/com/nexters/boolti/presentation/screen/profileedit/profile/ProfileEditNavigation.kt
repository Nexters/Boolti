package com.nexters.boolti.presentation.screen.profileedit.profile

import androidx.compose.ui.Modifier
import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import com.nexters.boolti.presentation.screen.LocalNavController
import com.nexters.boolti.presentation.screen.navigation.LinkListRoute
import com.nexters.boolti.presentation.screen.navigation.ProfileRoute
import com.nexters.boolti.presentation.screen.navigation.VideoListRoute

fun NavGraphBuilder.profileEditScreen(
    modifier: Modifier = Modifier,
) {
    composable<ProfileRoute.ProfileEdit> { backStackEntry ->
        val navController = LocalNavController.current

        ProfileEditScreen(
            modifier = modifier,
            navigateBack = navController::popBackStack,
            navigateToNicknameEdit = {
                navController.navigate(ProfileRoute.ProfileNicknameEdit)
            },
            navigateToUserCodeEdit = {
                navController.navigate(ProfileRoute.ProfileUserCodeEdit)
            },
            navigateToIntroductionEdit = {
                navController.navigate(ProfileRoute.ProfileIntroduceEdit)
            },
            navigateToSnsEdit = {
                navController.navigate(ProfileRoute.ProfileSnsEdit)
            },
            navigateToVideoEdit = { userCode ->
                navController.navigate(VideoListRoute.VideoListRoot(userCode, true))
            },
            navigateToLinkEdit = { userCode ->
                navController.navigate(LinkListRoute.LinkListRoot(userCode, true))
            },
        )
    }
}
