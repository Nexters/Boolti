package com.nexters.boolti.presentation.screen.link

import androidx.compose.ui.Modifier
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.navigation3.runtime.EntryProviderScope
import androidx.navigation3.runtime.NavKey
import com.nexters.boolti.presentation.screen.LocalBackStack
import com.nexters.boolti.presentation.screen.navigation.LinkListRoute
import com.nexters.boolti.presentation.screen.navigation.decorator.SharedViewModelStoreNavEntryDecorator
import com.nexters.boolti.presentation.screen.profileedit.link.LinkEditScreen

private val linkListContentKey: String = LinkListRoute.LinkList::class.qualifiedName ?: "LinkList"

fun EntryProviderScope<NavKey>.linkListScreen(
    modifier: Modifier = Modifier,
) {
    entry<LinkListRoute.LinkList>(
        clazzContentKey = { linkListContentKey },
    ) { key ->
        val viewModel = hiltViewModel<LinkListViewModel, LinkListViewModel.Factory>(
            creationCallback = { factory ->
                factory.create(key)
            }
        )
        val backStack = LocalBackStack.current

        LinkListScreen(
            modifier = modifier,
            navigateUp = backStack::removeLastOrNull,
            navigateToAddLink = {
                backStack.add(LinkListRoute.LinkEdit(isEditMode = true))
            },
            navigateToEditLink = {
                backStack.add(LinkListRoute.LinkEdit(isEditMode = false))
            },
            viewModel = viewModel,
        )
    }
}

fun EntryProviderScope<NavKey>.linkEditScreen(
    modifier: Modifier = Modifier,
) {
    entry<LinkListRoute.LinkEdit>(
        metadata = SharedViewModelStoreNavEntryDecorator.parent(linkListContentKey),
    ) {
        val backStack = LocalBackStack.current

        LinkEditScreen(
            modifier = modifier,
            navigateUp = backStack::removeLastOrNull,
        )
    }
}
