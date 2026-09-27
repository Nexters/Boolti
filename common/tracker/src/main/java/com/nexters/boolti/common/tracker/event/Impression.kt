package com.nexters.boolti.common.tracker.event

import com.nexters.boolti.common.tracker.AppTracker
import com.nexters.boolti.common.tracker.field.Role
import com.nexters.boolti.common.tracker.field.Screen
import com.nexters.boolti.common.tracker.impression.ImpressionEvent

/** 노출 상태에서 받은 이벤트를 기존 Impression 로그로 전달함. */
fun AppTracker.impression(
    event: ImpressionEvent,
    screen: Screen,
    objectRole: Role,
    objectValue: Any,
    withLogcat: Boolean = true,
) {
    require(event.extras.keys.none { it == "screen" || it == "object_role" || it == "object_value" }) {
        "Impression extras must not override screen, object_role or object_value"
    }
    impression(
        screen = screen,
        objectRole = objectRole,
        objectValue = objectValue,
        properties = event.extras,
        withLogcat = withLogcat,
    )
}

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
