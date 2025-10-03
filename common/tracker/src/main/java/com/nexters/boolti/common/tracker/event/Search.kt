package com.nexters.boolti.common.tracker.event

import com.nexters.boolti.common.tracker.AppTracker
import com.nexters.boolti.common.tracker.field.Screen

fun AppTracker.search(
    screen: Screen,
    keyword: String,
    properties: Map<String, Any> = emptyMap(),
) {
    trackEvent(
        eventName = "Search",
        properties = buildMap {
            put("screen", screen)
            put("search_keyword", keyword)
            putAll(properties)
        }
    )
}
