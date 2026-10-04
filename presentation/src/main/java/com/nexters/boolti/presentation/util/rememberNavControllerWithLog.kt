package com.nexters.boolti.presentation.util

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.navigation.NavBackStackEntry
import androidx.navigation.NavDestination
import androidx.navigation.NavHostController
import androidx.navigation.Navigator
import androidx.navigation.compose.rememberNavController
import com.google.firebase.Firebase
import com.google.firebase.analytics.FirebaseAnalytics
import com.google.firebase.analytics.analytics
import com.google.firebase.analytics.logEvent
import com.nexters.boolti.presentation.BuildConfig
import timber.log.Timber

@Composable
fun rememberNavControllerWithLog(
    vararg navigators: Navigator<out NavDestination>,
): NavHostController {
    val navController = rememberNavController(*navigators)

    LaunchedEffect(navController) {
        navController.currentBackStackEntryFlow
            .collect {
                val screenName = it.screenName()
                val args = it.argumentsText()

                if (BuildConfig.DEBUG) {
                    DebugManager.currentScreen = listOfNotNull(screenName, args).joinToString(" / ")
                }

                Timber.tag("MANGBAAM-(rememberNavControllerWithLog)")
                    .d("screenName: $screenName, arguments: $args")

                Firebase.analytics.logEvent(
                    FirebaseAnalytics.Event.SCREEN_VIEW,
                ) {
                    param(FirebaseAnalytics.Param.SCREEN_NAME, screenName)
                    args?.let { param("arguments", args) }
                }
            }
    }
    return navController
}

// ex1) report/{showId}
// ex2) com.nexters.boolti.presentation.screen.navigation.MainRoute.Home
internal fun NavBackStackEntry.screenName(): String =
    destination.route
        ?.substringBefore('/')
        ?.substringAfterLast(".")
        ?: ""

internal fun NavBackStackEntry.argumentsText(): String? =
    arguments?.keySet()?.fold(mutableMapOf<String, String>()) { map, key ->
        if (key == "android-support-nav:controller:deepLinkIntent") return@fold map
        map.apply {
            arguments?.get(key)?.let { arg -> put(key, arg.toString()) }
        }
    }?.ifEmpty { null }?.entries?.joinToString()
