package com.nexters.boolti.common.tracker.event

import com.nexters.boolti.common.tracker.AppTracker
import com.nexters.boolti.common.tracker.field.Link
import com.nexters.boolti.common.tracker.field.Role
import com.nexters.boolti.common.tracker.field.Screen

fun AppTracker.click(
    screen: Screen,
    objectRole: Role,
    objectValue: Any,
    properties: Map<String, Any> = emptyMap(),
    withLogcat: Boolean = true,
) {
    trackEvent(
        eventName = "Click",
        properties = buildMap {
            put("screen", screen.value)
            put("object_role", objectRole.value)
            put("object_value", objectValue)
            putAll(properties)
        },
        withLogcat = withLogcat,
    )
}

fun AppTracker.clickLink(
    screen: Screen,
    objectValue: Any,
    url: String,
    properties: Map<String, Any> = emptyMap(),
    withLogcat: Boolean = true,
) {
    click(
        screen = screen,
        objectRole = Role.Link,
        objectValue = objectValue,
        properties = buildMap {
            put("url", url)
            putAll(properties)
        },
        withLogcat = withLogcat
    )
}
