package com.nexters.boolti.presentation.screen.place

import io.kotest.core.spec.style.DescribeSpec
import io.kotest.matchers.shouldBe

class PlaceRegionTest : DescribeSpec({

    describe("regionOf") {
        it("시·도 표기가 달라도 같은 짧은 이름으로 바꾼다") {
            regionOf("서울특별시 마포구 와우산로 1") shouldBe "서울"
            regionOf("서울시 마포구 와우산로 1") shouldBe "서울"
            regionOf("서울 마포구 와우산로 1") shouldBe "서울"
        }

        it("도와 특별자치도도 짧은 이름으로 바꾼다") {
            regionOf("경기도 성남시 분당구") shouldBe "경기"
            regionOf("강원특별자치도 춘천시") shouldBe "강원"
            regionOf("전라북도 전주시") shouldBe "전북"
            regionOf("전북특별자치도 전주시") shouldBe "전북"
        }

        it("앞뒤 공백은 무시한다") {
            regionOf("  부산광역시 해운대구 ") shouldBe "부산"
        }

        it("주소가 없으면 null") {
            regionOf(null) shouldBe null
            regionOf("") shouldBe null
            regionOf("   ") shouldBe null
        }

        it("시·도로 시작하지 않으면 null") {
            regionOf("마포구 와우산로 1") shouldBe null
            regionOf("서울특별시마포구") shouldBe null
        }
    }
})
