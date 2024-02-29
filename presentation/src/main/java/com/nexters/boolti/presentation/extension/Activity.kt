package com.nexters.boolti.presentation.extension

import android.app.Activity
import android.content.Intent
import androidx.core.app.ActivityCompat

fun Activity.requestPermission(permission: String, requestCode: Int) {
    if (!checkGrantedPermission(permission)) {
        ActivityCompat.requestPermissions(
            this, arrayOf(permission),
            requestCode,
        )
    }
}

inline fun <reified T : Activity> Activity.startActivity(
    finishCallingActivity: Boolean = false,
    block: (Intent.() -> Unit),
) {
    val intent = Intent(this, T::class.java)
    block.invoke(intent)
    startActivity(intent)
    if (finishCallingActivity) finish()
}
