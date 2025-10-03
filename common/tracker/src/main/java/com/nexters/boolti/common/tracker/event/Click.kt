package com.nexters.boolti.common.tracker.event

import com.nexters.boolti.common.tracker.AppTracker
import com.nexters.boolti.common.tracker.field.Role
import com.nexters.boolti.common.tracker.field.Screen

fun AppTracker.click(
    screen: Screen,
    objectRole: Role,
    objectValue: Any,
    properties: Map<String, Any> = emptyMap(),
) {
    trackEvent(
        eventName = "Click",
        properties = buildMap {
            put("screen", screen)
            put("object_role", objectRole)
            put("object_value", objectValue)
            putAll(properties)
        }
    )
}
