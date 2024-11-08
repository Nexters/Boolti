package com.nexters.boolti.presentation.screen.profileedit.profile

import androidx.compose.ui.Modifier
import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import com.nexters.boolti.domain.model.Link
import com.nexters.boolti.presentation.screen.MainDestination
import kotlinx.coroutines.flow.filterNotNull
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.zip
import java.util.UUID

fun NavGraphBuilder.ProfileEditScreen(
    navigateTo: (String) -> Unit,
    popBackStack: () -> Unit,
    modifier: Modifier = Modifier,
) {
    composable(route = MainDestination.ProfileEdit.route) { backStackEntry ->
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

        val removeLinkId = backStackEntry.savedStateHandle
            .getStateFlow<String?>("removeLinkId", null).filterNotNull()
            .onEach {
                backStackEntry.savedStateHandle.remove<String>("removeLinkId")
            }

        ProfileEditScreen(
            modifier = modifier,
            navigateBack = popBackStack,
            navigateToSnsEdit = { sns ->
                sns?.let {
                    navigateTo(MainDestination.ProfileSnsEdit.createRoute(it))
                } ?: run {
                    navigateTo(MainDestination.ProfileSnsEdit.createRoute())
                }
            },
            navigateToLinkEdit = { link ->
                link?.let {
                    navigateTo(MainDestination.ProfileLinkEdit.createRoute(it))
                } ?: run {
                    navigateTo(MainDestination.ProfileLinkEdit.createRoute())
                }
            },
            newLinkCallback = newLink,
            editLinkCallback = editLink,
            removeLinkCallback = removeLinkId,
        )
    }
}
