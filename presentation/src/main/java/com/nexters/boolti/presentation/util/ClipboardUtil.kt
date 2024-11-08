package com.nexters.boolti.presentation.util

import android.annotation.SuppressLint
import android.os.Build
import androidx.compose.material3.SnackbarHostState
import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.ClipboardManager
import androidx.compose.ui.platform.LocalClipboardManager
import androidx.compose.ui.text.AnnotatedString
import com.nexters.boolti.presentation.screen.LocalSnackbarController
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch

fun String.copyToClipboard(
    clipboardManager: ClipboardManager,
    scope: CoroutineScope,
    copySuccessMessage: String? = null,
    snackbarHostState: SnackbarHostState? = null,
) {
    copyMessage(
        message = this,
        copySuccessMessage = copySuccessMessage,
        clipboardManager = clipboardManager,
        snackbarHostState = snackbarHostState,
        scope = scope,
    )
}

fun copyMessage(
    message: AnnotatedString,
    copySuccessMessage: String? = null,
    clipboardManager: ClipboardManager,
    snackbarHostState: SnackbarHostState? = null,
    scope: CoroutineScope,
) {
    clipboardManager.setText(message)
    if (copySuccessMessage != null && snackbarHostState != null && Build.VERSION.SDK_INT <= Build.VERSION_CODES.S_V2) {
        scope.launch {
            snackbarHostState.showSnackbar(copySuccessMessage)
        }
    }
}

fun copyMessage(
    message: String,
    copySuccessMessage: String? = null,
    clipboardManager: ClipboardManager,
    snackbarHostState: SnackbarHostState? = null,
    scope: CoroutineScope,
) {
    copyMessage(
        message = AnnotatedString(message),
        copySuccessMessage = copySuccessMessage,
        clipboardManager = clipboardManager,
        snackbarHostState = snackbarHostState,
        scope = scope
    )
}

@SuppressLint("ComposableNaming")
@Composable
fun copyMessage(
    message: AnnotatedString,
    copySuccessMessage: String? = null,
    clipboardManager: ClipboardManager = LocalClipboardManager.current,
    snackbarHostState: SnackbarController = LocalSnackbarController.current
) {
    clipboardManager.setText(message)
    if (copySuccessMessage != null && Build.VERSION.SDK_INT <= Build.VERSION_CODES.S_V2) {
        snackbarHostState.showMessage(copySuccessMessage)
    }
}
