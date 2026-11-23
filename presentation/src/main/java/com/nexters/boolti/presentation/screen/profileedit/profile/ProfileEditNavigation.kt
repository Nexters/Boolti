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
            navigateUp = navController::popBackStack,
            navigateToNicknameEdit = { nickname ->
                navController.navigate(ProfileRoute.ProfileNicknameEdit(nickname))
            },
            navigateToUserCodeEdit = { userCode ->
                navController.navigate(ProfileRoute.ProfileUserCodeEdit(userCode))
            },
            navigateToIntroductionEdit = { introduction ->
                navController.navigate(ProfileRoute.ProfileIntroduceEdit(introduction))
            },
            navigateToSnsEdit = { instagramUsername, youtubeUsername ->
                navController.navigate(ProfileRoute.ProfileSnsEdit(instagramUsername, youtubeUsername))
            },
            navigateToVideoEdit = { userCode ->
                navController.navigate(VideoListRoute.VideoList(userCode, true))
            },
            navigateToLinkEdit = { userCode ->
                navController.navigate(LinkListRoute.LinkList(userCode, true))
            },
        )
    }
}
