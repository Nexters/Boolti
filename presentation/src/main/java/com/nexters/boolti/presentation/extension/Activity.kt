package com.nexters.boolti.presentation.extension

import android.app.Activity
import androidx.core.app.ActivityCompat

fun Activity.requestPermission(permission: String, requestCode: Int) {
    if (!checkGranted(permission)) {
        ActivityCompat.requestPermissions(
            this, arrayOf(permission),
            requestCode,
        )
    }
}
