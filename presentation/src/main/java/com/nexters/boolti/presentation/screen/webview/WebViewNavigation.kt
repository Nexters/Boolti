package com.nexters.boolti.presentation.screen.webview

import androidx.compose.ui.Modifier
import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import com.nexters.boolti.presentation.screen.MainDestination

fun NavGraphBuilder.addWebViewScreen(
    modifier: Modifier = Modifier,
) {
    composable(
        route = MainDestination.WebView.route,
        arguments = MainDestination.Refund.arguments,
    ) { entry ->
        val url = checkNotNull(entry.arguments?.getString("url")) {
            "Url for WebViewScreen was null."
        }
        WebViewScreen(
            modifier = modifier,
            url = url,
        )
    }
}
