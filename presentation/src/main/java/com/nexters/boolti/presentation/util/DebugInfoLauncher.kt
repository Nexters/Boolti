package com.nexters.boolti.presentation.util

import android.content.Context
import android.content.Intent
import androidx.navigation.NavController
import com.nexters.boolti.presentation.BuildConfig

const val EXTRA_CURRENT_SCREEN = "currentScreen"

/**
 * 디버그 정보 팝업(debug 소스셋의 DebugInfoActivity)을 띄운다. debug 빌드에서만 부른다
 */
fun openDebugInfo(context: Context, navController: NavController) {
    val currentScreen = navController.currentBackStackEntry?.let { entry ->
        listOfNotNull(entry.screenName(), entry.argumentsText()).joinToString(" / ")
    }
    context.startActivity(
        Intent()
            .setClassName(context, "${BuildConfig.LIBRARY_PACKAGE_NAME}.screen.debug.info.DebugInfoActivity")
            .putExtra(EXTRA_CURRENT_SCREEN, currentScreen)
    )
}
