package com.nexters.boolti.common.tracker.field

import com.nexters.boolti.common.tracker.TrackerField

@JvmInline
value class Role(
    override val value: String,
) : TrackerField {
    companion object
}

val Role.Companion.Button
    get() = Role("Button")

val Role.Companion.BottomSheet
    get() = Role("BottomSheet")

val Role.Companion.Popup
    get() = Role("Popup")

val Role.Companion.Banner
    get() = Role("Banner")

val Role.Companion.Link
    get() = Role("Link")

val Role.Companion.Item
    get() = Role("Item")

val Role.Companion.Tab
    get() = Role("Tab")

val Role.Companion.Chip
    get() = Role("Chip")
