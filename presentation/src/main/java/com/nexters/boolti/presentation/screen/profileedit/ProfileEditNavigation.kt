package com.nexters.boolti.presentation.screen.profileedit

import androidx.compose.ui.Modifier
import androidx.navigation3.runtime.EntryProviderScope
import androidx.navigation3.runtime.NavKey
import com.nexters.boolti.presentation.screen.LocalBackStack
import com.nexters.boolti.presentation.screen.navigation.LinkListRoute
import com.nexters.boolti.presentation.screen.navigation.ProfileRoute
import com.nexters.boolti.presentation.screen.navigation.VideoListRoute
import com.nexters.boolti.presentation.screen.profileedit.introduce.IntroduceEditScreen
import com.nexters.boolti.presentation.screen.profileedit.nickname.NicknameEditScreen
import com.nexters.boolti.presentation.screen.profileedit.profile.ProfileEditScreen
import com.nexters.boolti.presentation.screen.profileedit.sns.SnsEditScreen
import com.nexters.boolti.presentation.screen.profileedit.usercode.UserCodeEditScreen

fun EntryProviderScope<NavKey>.profileEditScreen(
    modifier: Modifier = Modifier,
) {
    entry<ProfileRoute.ProfileEdit> {
        val backStack = LocalBackStack.current

        ProfileEditScreen(
            modifier = modifier,
            navigateBack = backStack::removeLastOrNull,
            navigateToNicknameEdit = {
                backStack.add(ProfileRoute.ProfileNicknameEdit)
            },
            navigateToUserCodeEdit = {
                backStack.add(ProfileRoute.ProfileUserCodeEdit)
            },
            navigateToIntroductionEdit = {
                backStack.add(ProfileRoute.ProfileIntroduceEdit)
            },
            navigateToSnsEdit = {
                backStack.add(ProfileRoute.ProfileSnsEdit)
            },
            navigateToVideoEdit = { userCode ->
                backStack.add(VideoListRoute.VideoList(userCode, true))
            },
            navigateToLinkEdit = { userCode ->
                backStack.add(LinkListRoute.LinkListRoot(userCode, true))
            },
        )
    }

    entry<ProfileRoute.ProfileNicknameEdit> {
        val backStack = LocalBackStack.current

        NicknameEditScreen(
            modifier = modifier,
            navigateUp = backStack::removeLastOrNull,
        )
    }

    entry<ProfileRoute.ProfileUserCodeEdit> {
        val backStack = LocalBackStack.current

        UserCodeEditScreen(
            navigateUp = backStack::removeLastOrNull,
        )
    }

    entry<ProfileRoute.ProfileIntroduceEdit> {
        val backStack = LocalBackStack.current

        IntroduceEditScreen(
            navigateUp = backStack::removeLastOrNull,
        )
    }

    entry<ProfileRoute.ProfileSnsEdit> {
        val backStack = LocalBackStack.current

        SnsEditScreen(
            navigateUp = backStack::removeLastOrNull,
        )
    }
}
