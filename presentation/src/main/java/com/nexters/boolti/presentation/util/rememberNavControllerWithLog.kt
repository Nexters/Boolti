package com.nexters.boolti.presentation.util

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.core.os.bundleOf
import androidx.navigation.NavDestination
import androidx.navigation.NavHostController
import androidx.navigation.Navigator
import androidx.navigation.compose.rememberNavController
import com.google.firebase.analytics.ktx.analytics
import com.google.firebase.ktx.Firebase
import timber.log.Timber

@Composable
fun rememberNavControllerWithLog(
    vararg navigators: Navigator<out NavDestination>,
): NavHostController {
    val navController = rememberNavController(*navigators)

    LaunchedEffect(navController) {
        navController.currentBackStackEntryFlow
            .collect {
                val args = it.arguments?.keySet()?.fold(mutableMapOf<String, String>()) { map, key ->
                    map.apply {
                        it.arguments?.get(key)?.let { put(key, it.toString()) }
                    }
                }?.filterKeys { it != "android-support-nav:controller:deepLinkIntent" }

                Timber.tag("MANGBAAM-(rememberNavControllerWithLog)")
                    .d("route: ${it.destination.route}, arguments: $args")

                Firebase.analytics.logEvent(
                    "screen",
                    bundleOf(
                        "route" to it.destination.route,
                        "arguments" to args
                    ),
                )
            }
    }
    return navController
}
