package com.nexters.boolti.presentation.util

import androidx.compose.material3.SnackbarHostState
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.launch

class SnackbarController(
    private val snackbarHostState: SnackbarHostState,
    private val coroutineScope: CoroutineScope = CoroutineScope(SupervisorJob()),
) {
    fun showMessage(
        message: String,
        dismissPrevious: Boolean = true,
    ) {
        coroutineScope.launch {
            if (dismissPrevious) snackbarHostState.currentSnackbarData?.dismiss()
            snackbarHostState.showSnackbar(message)
        }
    }
}
