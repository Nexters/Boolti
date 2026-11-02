package com.nexters.boolti.data.repository

import android.os.BatteryManager
import io.kotest.core.spec.style.DescribeSpec
import io.kotest.matchers.shouldBe
import java.util.Locale

class DeviceInfoFormatTest : DescribeSpec({
    describe("formatStorageFree") {
        val gb = 1024L * 1024 * 1024
        val mb = 1024L * 1024

        it("GB 단위 소수 첫째 자리까지 표시한다") {
            formatStorageFree(3 * gb + 512 * mb) shouldBe "3.5GB"
            formatStorageFree(3 * gb + 5 * mb) shouldBe "3.0GB"
            formatStorageFree(0) shouldBe "0.0GB"
        }

        it("기기 언어가 소수점으로 쉼표를 써도 '.'으로 표시한다") {
            val original = Locale.getDefault()
            Locale.setDefault(Locale.GERMANY)
            try {
                formatStorageFree(3 * gb + 512 * mb) shouldBe "3.5GB"
            } finally {
                Locale.setDefault(original)
            }
        }
    }

    describe("formatBattery") {
        it("상태와 퍼센트를 표시한다") {
            formatBattery(50, 100, BatteryManager.BATTERY_STATUS_CHARGING) shouldBe "charging 50%"
            formatBattery(1, 2, BatteryManager.BATTERY_STATUS_FULL) shouldBe "full 50%"
            formatBattery(80, 100, -1) shouldBe "unknown 80%"
        }

        it("잔량을 읽지 못하면 null") {
            formatBattery(-1, 100, BatteryManager.BATTERY_STATUS_CHARGING) shouldBe null
            formatBattery(50, 0, BatteryManager.BATTERY_STATUS_CHARGING) shouldBe null
        }
    }
})
