package com.nexters.boolti.common.tracker.event

import com.nexters.boolti.common.tracker.AppTracker
import com.nexters.boolti.common.tracker.field.Screen

fun AppTracker.view(
    screen: Screen,
    properties: Map<String, Any> = emptyMap(),
) {
    trackEvent(
        eventName = "View Screen",
        properties = buildMap {
            put("screen", screen)
            putAll(properties)
        }
    )
}
