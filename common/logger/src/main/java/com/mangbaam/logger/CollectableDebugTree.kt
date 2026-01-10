package com.mangbaam.logger

import timber.log.Timber

class CollectableDebugTree : Timber.DebugTree() {
    override fun log(priority: Int, tag: String?, message: String, t: Throwable?) {
        super.log(priority, tag, message, t)

        LogCollector.addLog(
            LogData(
                tag = tag,
                message = message,
                level = priority,
            )
        )
    }
}
