package com.nexters.boolti.common.tracker.field

import com.nexters.boolti.common.tracker.TrackerField

@JvmInline
value class Screen(
    override val value: String,
) : TrackerField {
    companion object
}

val Screen.Companion.Login
    get() = Screen("Login")

val Screen.Companion.Home
    get() = Screen("Home")

val Screen.Companion.ShowDetail
    get() = Screen("ShowDetail")

val Screen.Companion.Payment
    get() = Screen("Payment")

val Screen.Companion.Profile
    get() = Screen("Profile")

val Screen.Companion.ProfileEdit
    get() = Screen("ProfileEdit")

val Screen.Companion.MyPage
    get() = Screen("MyPage")

val Screen.RegisterShow
    get() = Screen("RegisterShow")
