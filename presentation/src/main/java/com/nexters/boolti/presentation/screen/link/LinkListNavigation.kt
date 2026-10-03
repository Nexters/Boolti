package com.nexters.boolti.presentation.screen.link

import androidx.compose.ui.Modifier
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import com.nexters.boolti.presentation.screen.LocalNavController
import com.nexters.boolti.presentation.screen.navigation.LinkListRoute
import com.nexters.boolti.presentation.screen.profileedit.link.LinkEditResult
import com.nexters.boolti.presentation.util.ObserveNavResult

fun NavGraphBuilder.linkListScreen(
    modifier: Modifier = Modifier,
) {
    composable<LinkListRoute.LinkList> { entry ->
        val navController = LocalNavController.current
        val viewModel: LinkListViewModel = hiltViewModel()
        ObserveNavResult<LinkEditResult>(entry.savedStateHandle, LinkEditResult.KEY) {
            viewModel.onAction(LinkListAction.EditResultReceived(it))
        }
        LinkListScreen(
            modifier = modifier,
            viewModel = viewModel,
            navigateUp = navController::navigateUp,
            navigateToAddLink = { closeListOnBack ->
                navController.navigate(LinkListRoute.LinkEdit(closeListOnBack = closeListOnBack))
            },
            navigateToEditLink = { link ->
                navController.navigate(LinkListRoute.LinkEdit(linkId = link.id, name = link.name, url = link.url))
            },
        )
    }
}
