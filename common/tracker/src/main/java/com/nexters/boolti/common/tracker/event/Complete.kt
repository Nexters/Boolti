package com.nexters.boolti.common.tracker.event

import com.nexters.boolti.common.tracker.AppTracker

fun AppTracker.complete(
    target: String,
    properties: Map<String, Any> = emptyMap(),
    trimPrefix: Boolean = true,
    withLogcat: Boolean = true,
) {
    val eventName =
        "$COMPLETE_PREFIX${(if (trimPrefix) target.substringAfter(COMPLETE_PREFIX) else target)}"

    trackEvent(
        eventName = eventName,
        properties = properties,
        withLogcat = withLogcat,
    )
}

private const val COMPLETE_PREFIX = "Complete "
