package com.nexters.boolti.presentation.util

import android.app.Activity
import android.content.Context
import android.content.ContextWrapper

fun Context.requireActivity(): Activity {
    var ctx = this
    while (ctx is ContextWrapper) {
        if (ctx is Activity) {
            return ctx
        }
        ctx = ctx.baseContext
    }

    throw IllegalStateException(
        "Expected an activity context for retrieving an activity " +
                "but instead found: $ctx"
    )
}
