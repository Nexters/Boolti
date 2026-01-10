package com.nexters.boolti.common.tracker.event

import com.nexters.boolti.common.tracker.AppTracker
import com.nexters.boolti.common.tracker.field.Role
import com.nexters.boolti.common.tracker.field.Screen

fun AppTracker.impression(
    screen: Screen,
    objectRole: Role,
    objectValue: Any,
    properties: Map<String, Any> = emptyMap(),
    withLogcat: Boolean = true,
) {
    trackEvent(
        eventName = "Impression",
        properties = buildMap {
            put("screen", screen.value)
            put("object_role", objectRole.value)
            put("object_value", objectValue)
            putAll(properties)
        },
        withLogcat = withLogcat,
    )
}
