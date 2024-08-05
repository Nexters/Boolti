package com.nexters.boolti.presentation.util

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.navigation.NavDestination
import androidx.navigation.NavHostController
import androidx.navigation.Navigator
import androidx.navigation.compose.rememberNavController
import com.google.firebase.analytics.FirebaseAnalytics
import com.google.firebase.analytics.ktx.analytics
import com.google.firebase.analytics.logEvent
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
                val screenName = it.destination.route?.substringBefore('/') ?: ""
                val args = it.arguments?.keySet()?.fold(mutableMapOf<String, String>()) { map, key ->
                    if (key == "android-support-nav:controller:deepLinkIntent") return@fold map
                    map.apply {
                        it.arguments?.get(key)?.let { arg -> put(key, arg.toString()) }
                    }
                }?.ifEmpty { null }?.entries?.joinToString()

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
