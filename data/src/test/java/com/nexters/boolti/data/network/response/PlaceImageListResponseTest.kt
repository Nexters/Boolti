package com.nexters.boolti.data.network.response

import io.kotest.core.spec.style.DescribeSpec
import io.kotest.matchers.shouldBe

class PlaceImageListResponseTest : DescribeSpec({

    describe("PlaceImageResponseTest.toDomain") {
        context("정상 응답이 주어졌을 때") {
            val response = PlaceImageListResponse(
                items = listOf(
                    PlaceImageItemResponse(
                        id = "13",
                        imageUrl = "https://image.boolti.in/13",
                        thumbnailUrl = "https://thumbnail.boolti.in/13",
                        sequence = 2,
                    ),
                    PlaceImageItemResponse(
                        id = "11",
                        imageUrl = "https://image.boolti.in/11",
                        thumbnailUrl = "https://thumbnail.boolti.in/11",
                        sequence = 0,
                    ),
                ),
            )

            it("sequence 오름차순으로 정렬한다") {
                response.toDomain().map { it.id } shouldBe listOf("11", "13")
            }

            it("이미지 URL을 그대로 매핑한다") {
                val image = response.toDomain().first()
                image.imageUrl shouldBe "https://image.boolti.in/11"
                image.thumbnailUrl shouldBe "https://thumbnail.boolti.in/11"
            }
        }
    }
})
