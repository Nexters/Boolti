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
    copiedMessage: String? = null,
    snackbarHostState: SnackbarHostState? = null,
) {
    copyMessage(
        message = this,
        copiedMessage = copiedMessage,
        clipboardManager = clipboardManager,
        snackbarHostState = snackbarHostState,
        scope = scope,
    )
}

fun copyMessage(
    message: AnnotatedString,
    copiedMessage: String? = null,
    clipboardManager: ClipboardManager,
    snackbarHostState: SnackbarHostState? = null,
    scope: CoroutineScope,
) {
    clipboardManager.setText(message)
    if (copiedMessage != null && snackbarHostState != null && Build.VERSION.SDK_INT <= Build.VERSION_CODES.S_V2) {
        scope.launch {
            snackbarHostState.showSnackbar(copiedMessage)
        }
    }
}

fun copyMessage(
    message: String,
    copiedMessage: String? = null,
    clipboardManager: ClipboardManager,
    snackbarHostState: SnackbarHostState? = null,
    scope: CoroutineScope,
) {
    copyMessage(
        message = AnnotatedString(message),
        copiedMessage = copiedMessage,
        clipboardManager = clipboardManager,
        snackbarHostState = snackbarHostState,
        scope = scope
    )
}

@SuppressLint("ComposableNaming")
@Composable
fun copyMessage(
    message: AnnotatedString,
    copiedMessage: String? = null,
    clipboardManager: ClipboardManager = LocalClipboardManager.current,
    snackbarHostState: SnackbarController = LocalSnackbarController.current
) {
    clipboardManager.setText(message)
    if (copiedMessage != null && Build.VERSION.SDK_INT <= Build.VERSION_CODES.S_V2) {
        snackbarHostState.showMessage(copiedMessage)
    }
}
