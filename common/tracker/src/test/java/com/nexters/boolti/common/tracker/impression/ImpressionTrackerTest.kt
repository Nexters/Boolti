package com.nexters.boolti.common.tracker.impression

import com.nexters.boolti.common.tracker.AppTracker
import com.nexters.boolti.common.tracker.event.impression
import com.nexters.boolti.common.tracker.field.Home
import com.nexters.boolti.common.tracker.field.Item
import com.nexters.boolti.common.tracker.field.Role
import com.nexters.boolti.common.tracker.field.Screen
import io.kotest.assertions.throwables.shouldThrow
import io.kotest.core.spec.style.FunSpec

class ImpressionTrackerTest : FunSpec({
    listOf("screen", "object_role", "object_value").forEach { reservedKey ->
        test("extras가 $reservedKey 필드를 덮어쓰면 전송 전에 거부함") {
            shouldThrow<IllegalArgumentException> {
                AppTracker.impression(
                    event = ImpressionEvent("a", 0.5f, mapOf(reservedKey to "Invalid")),
                    screen = Screen.Home,
                    objectRole = Role.Item,
                    objectValue = "ShowCard",
                )
            }
        }
    }
})
