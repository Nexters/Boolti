package com.nexters.boolti.presentation.base

import androidx.lifecycle.ViewModel
import com.google.firebase.crashlytics.FirebaseCrashlytics
import kotlinx.coroutines.CoroutineExceptionHandler
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.cancel
import kotlinx.coroutines.launch
import timber.log.Timber

open class BaseViewModel : ViewModel() {
    private val analyticsScope = CoroutineScope(SupervisorJob() + Dispatchers.IO)
    protected val recordExceptionHandler = CoroutineExceptionHandler { _, throwable ->
        analyticsScope.launch {
            FirebaseCrashlytics.getInstance().recordException(throwable)
            Timber.e(throwable)
        }
    }

    override fun onCleared() {
        super.onCleared()
        analyticsScope.cancel()
    }
}
