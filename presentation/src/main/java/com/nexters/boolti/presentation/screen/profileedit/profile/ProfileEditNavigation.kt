package com.nexters.boolti.presentation.screen.profileedit.profile

import androidx.compose.ui.Modifier
import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import com.nexters.boolti.domain.model.Link
import com.nexters.boolti.domain.model.Sns
import com.nexters.boolti.presentation.screen.LocalNavController
import com.nexters.boolti.presentation.screen.navigation.ProfileRoute
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.filterNotNull
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.zip
import java.util.UUID

fun NavGraphBuilder.profileEditScreen(
    modifier: Modifier = Modifier,
) {
    composable<ProfileRoute.ProfileEdit> { backStackEntry ->
        val navController = LocalNavController.current

        // 새 링크 추가
        val newLinkName = backStackEntry.savedStateHandle
            .getStateFlow<String?>("newLinkName", null).filterNotNull()
        val newLinkUrl = backStackEntry.savedStateHandle
            .getStateFlow<String?>("newLinkUrl", null).filterNotNull()

        val newLink = newLinkName.zip(newLinkUrl) { name, url ->
            Link(
                id = UUID.randomUUID().toString(),
                name = name,
                url = url,
            )
        }.onEach {
            backStackEntry.savedStateHandle.remove<String>("newLinkName")
            backStackEntry.savedStateHandle.remove<String>("newLinkUrl")
        }

        // 기존 링크 수정
        val editLinkId = backStackEntry.savedStateHandle
            .getStateFlow<String?>("editLinkId", null).filterNotNull()
        val editLinkName = backStackEntry.savedStateHandle
            .getStateFlow<String?>("editLinkName", null).filterNotNull()
        val editLinkUrl = backStackEntry.savedStateHandle
            .getStateFlow<String?>("editLinkUrl", null).filterNotNull()
        val editLink = editLinkName.zip(editLinkUrl) { name, url ->
            name to url
        }.zip(editLinkId) { (name, url), id ->
            Link(id, name, url)
        }.onEach {
            backStackEntry.savedStateHandle.apply {
                remove<String>("editLinkId")
                remove<String>("editLinkName")
                remove<String>("editLinkUrl")
            }
        }

        // 링크 제거
        val removeLinkId = backStackEntry.savedStateHandle
            .getStateFlow<String?>("removeLinkId", null).filterNotNull()
            .onEach {
                backStackEntry.savedStateHandle.remove<String>("removeLinkId")
            }

        // 새 SNS 추가
        val newSnsType = backStackEntry.savedStateHandle
            .getStateFlow<Sns.SnsType?>("newSnsType", null)
            .filterNotNull()

        val newSnsUsername = backStackEntry.savedStateHandle
            .getStateFlow<String?>("newSnsUsername", null).filterNotNull()

        val newSns = newSnsType.zip(newSnsUsername) { type, username ->
            Sns(
                id = UUID.randomUUID().toString(),
                type = type,
                username = username,
            )
        }.onEach {
            backStackEntry.savedStateHandle.remove<Sns.SnsType>("newSnsType")
            backStackEntry.savedStateHandle.remove<String>("newSnsUsername")
        }

        // 기존 SNS 수정
        val editSnsId = backStackEntry.savedStateHandle
            .getStateFlow<String?>("editSnsId", null).filterNotNull()
        val editSnsType = backStackEntry.savedStateHandle
            .getStateFlow<Sns.SnsType?>("editSnsType", null)
            .filterNotNull()
        val editSnsUsername = backStackEntry.savedStateHandle
            .getStateFlow<String?>("editSnsUsername", null).filterNotNull()
        val editedSns = combine(
            editSnsId,
            editSnsType,
            editSnsUsername,
        ) { id, type, username ->
            Sns(id = id, type = type, username = username)
        }.onEach {
            backStackEntry.savedStateHandle.remove<String>("editSnsId")
            backStackEntry.savedStateHandle.remove<Sns.SnsType>("editSnsType")
            backStackEntry.savedStateHandle.remove<String>("editSnsUsername")
        }

        // SNS 제거
        val removedSnsId = backStackEntry.savedStateHandle
            .getStateFlow<String?>("removeSnsId", null).filterNotNull()
            .onEach {
                backStackEntry.savedStateHandle.remove<String>("removeSnsId")
            }

        ProfileEditScreen(
            modifier = modifier,
            navigateBack = navController::popBackStack,
            navigateToNicknameEdit = {
                navController.navigate(ProfileRoute.ProfileNicknameEdit)
            },
            navigateToUserCodeEdit = {
                navController.navigate(ProfileRoute.ProfileUserCodeEdit)
            },
            navigateToSnsEdit = { sns ->
                navController.navigate(ProfileRoute.ProfileSnsEdit(sns))
            },
            navigateToLinkEdit = { link ->
                navController.navigate(ProfileRoute.ProfileLinkEdit(link))
            },
            newLinkCallback = newLink,
            editLinkCallback = editLink,
            removeLinkCallback = removeLinkId,
            newSnsCallback = newSns,
            editSnsCallback = editedSns,
            removeSnsCallback = removedSnsId,
        )
    }
}
