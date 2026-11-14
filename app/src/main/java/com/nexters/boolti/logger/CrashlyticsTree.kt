package com.nexters.boolti.logger

import android.util.Log
import com.google.firebase.Firebase
import com.google.firebase.crashlytics.crashlytics
import timber.log.Timber
import java.io.IOException
import kotlin.coroutines.cancellation.CancellationException

class CrashlyticsTree : Timber.Tree() {
    override fun log(priority: Int, tag: String?, message: String, t: Throwable?) {
        if (t != null && shouldRecord(priority, t)) Firebase.crashlytics.recordException(t)
    }

    companion object {
        // 네트워크 끊김·코루틴 취소는 앱 결함이 아니라서 제외해요
        internal fun shouldRecord(priority: Int, t: Throwable): Boolean =
            priority >= Log.ERROR && t !is IOException && t !is CancellationException
    }
}
