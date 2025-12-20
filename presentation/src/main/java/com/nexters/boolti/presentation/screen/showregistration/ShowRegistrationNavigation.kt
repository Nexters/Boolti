package com.nexters.boolti.presentation.screen.showregistration

import androidx.compose.ui.Modifier
import androidx.navigation3.runtime.EntryProviderScope
import androidx.navigation3.runtime.NavKey
import com.nexters.boolti.presentation.screen.LocalBackStack
import com.nexters.boolti.presentation.screen.navigation.MainRoute

fun EntryProviderScope<NavKey>.showRegistrationScreen(
    modifier: Modifier = Modifier,
) {
    entry<MainRoute.ShowRegistration> {
        val backStack = LocalBackStack.current

        ShowRegistrationScreen(
            modifier = modifier,
            onClickBack = backStack::removeLastOrNull,
            navigateTo = { route ->
                backStack.add(route)
            },
            navigateToHome = {
                backStack.clear()
                backStack.add(MainRoute.Home)
            },
        )
    }
}
