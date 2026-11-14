package com.nexters.boolti.logger

import android.util.Log
import io.kotest.core.spec.style.BehaviorSpec
import io.kotest.matchers.shouldBe
import java.io.IOException
import kotlin.coroutines.cancellation.CancellationException

class CrashlyticsTreeTest : BehaviorSpec() {
    init {
        given("ERROR 로그에 예외가 주어지고") {
            then("일반 예외는 기록하고, IOException·CancellationException은 제외한다") {
                CrashlyticsTree.shouldRecord(Log.ERROR, IllegalStateException()) shouldBe true
                CrashlyticsTree.shouldRecord(Log.ERROR, IOException()) shouldBe false
                CrashlyticsTree.shouldRecord(Log.ERROR, CancellationException()) shouldBe false
            }
        }

        given("ERROR 미만 로그에 예외가 주어지고") {
            then("기록하지 않는다") {
                CrashlyticsTree.shouldRecord(Log.WARN, IllegalStateException()) shouldBe false
            }
        }
    }
}
