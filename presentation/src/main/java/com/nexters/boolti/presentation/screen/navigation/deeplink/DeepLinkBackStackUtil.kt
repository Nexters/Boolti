package com.nexters.boolti.presentation.screen.navigation.deeplink

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.net.Uri
import androidx.core.app.TaskStackBuilder
import androidx.core.net.toUri
import androidx.navigation3.runtime.NavBackStack
import androidx.navigation3.runtime.NavKey
import com.nexters.boolti.presentation.screen.navigation.HomeRoute

internal fun buildBackStack(
    startKey: NavKey,
    buildFullPath: Boolean,
): List<NavKey> {
    if (!buildFullPath) return listOf(startKey)

    return buildList {
        var node: NavKey? = startKey
        while (node != null) {
            add(0, node)
            val parent = if (node is NavDeepLinkKey) {
                node.parent
            } else {
                null
            }

            node = parent
        }
    }
}

internal fun NavBackStack<NavKey>.navigateUp(
    activity: Activity,
    context: Context,
) {
    if (size == 1) {
        val currKey = last()
        val deeplinkKey = if (currKey is NavDeepLinkKey) {
            currKey.parent
        } else {
            null
        }

        val builder = createTaskStackBuilder(deeplinkKey, activity, context)
        activity.finish()
        builder.startActivities()
    } else {
        removeLastOrNull()
    }
}

private fun createTaskStackBuilder(
    deeplinkKey: NavKey?,
    activity: Activity,
    context: Context,
): TaskStackBuilder {
    val intent = Intent(context, activity.javaClass)

    if (deeplinkKey != null && deeplinkKey is NavDeepLinkKey) {
        intent.data = deeplinkKey.deeplinkUrl.toUri()
    }

    intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK)

    return TaskStackBuilder.create(context).addNextIntentWithParentStack(intent)
}

internal fun Uri?.toKey(): NavKey {
    if (this == null) return HomeRoute.Show

    val paths = pathSegments

    if (paths.isEmpty()) return HomeRoute.Show

    return when (paths.first()) {
        DEEPLINK_SEARCH -> HomeRoute.Search
        DEEPLINK_TICKETS -> HomeRoute.Ticket
        DEEPLINK_SHOWS -> HomeRoute.Show
        else -> HomeRoute.Show
    }
}
