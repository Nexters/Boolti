package com.nexters.boolti.presentation.base

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.crashlytics.FirebaseCrashlytics
import kotlinx.coroutines.CoroutineExceptionHandler
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import timber.log.Timber

open class BaseViewModel : ViewModel() {
    protected val recordExceptionHandler = CoroutineExceptionHandler { _, throwable ->
        viewModelScope.launch(Dispatchers.IO) {
            FirebaseCrashlytics.getInstance().recordException(throwable)
            Timber.e(throwable)
        }
    }
}
