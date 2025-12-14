package com.nexters.boolti.presentation.screen.navigation.deeplink

import androidx.navigation3.runtime.NavKey

internal interface NavDeepLinkKey : NavKey {
    val parent: NavKey?
    val deeplinkUrl: String
}
