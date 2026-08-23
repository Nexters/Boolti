package com.nexters.boolti.data.network.response

import io.kotest.core.spec.style.DescribeSpec
import io.kotest.matchers.collections.shouldBeEmpty
import io.kotest.matchers.shouldBe

class ConcertHallImageResponseTest : DescribeSpec({

    describe("ConcertHallImageListResponse.toDomain") {
        context("정상 응답이 주어졌을 때") {
            val response = ConcertHallImageListResponse(
                items = listOf(
                    ConcertHallImageItemResponse(
                        id = 13,
                        imageUrl = "https://image.boolti.in/13",
                        thumbnailUrl = "https://thumbnail.boolti.in/13",
                        sequence = 2,
                    ),
                    ConcertHallImageItemResponse(
                        id = 11,
                        imageUrl = "https://image.boolti.in/11",
                        thumbnailUrl = "https://thumbnail.boolti.in/11",
                        sequence = 0,
                    ),
                ),
            )

            it("sequence 오름차순으로 정렬한다") {
                response.toDomain().map { it.id } shouldBe listOf(11L, 13L)
            }

            it("이미지 URL을 그대로 매핑한다") {
                val image = response.toDomain().first()
                image.imageUrl shouldBe "https://image.boolti.in/11"
                image.thumbnailUrl shouldBe "https://thumbnail.boolti.in/11"
            }
        }

        context("thumbnailUrl이 없을 때") {
            val response = ConcertHallImageListResponse(
                items = listOf(
                    ConcertHallImageItemResponse(
                        id = 11,
                        imageUrl = "https://image.boolti.in/11",
                        thumbnailUrl = null,
                        sequence = 0,
                    ),
                ),
            )

            it("원본 이미지 URL을 썸네일로 사용한다") {
                response.toDomain().first().thumbnailUrl shouldBe "https://image.boolti.in/11"
            }
        }

        context("imageUrl이 없을 때") {
            val response = ConcertHallImageListResponse(
                items = listOf(
                    ConcertHallImageItemResponse(
                        id = 11,
                        imageUrl = null,
                        thumbnailUrl = "https://thumbnail.boolti.in/11",
                        sequence = 0,
                    ),
                ),
            )

            it("해당 이미지를 제외한다") {
                response.toDomain().shouldBeEmpty()
            }
        }

        context("items가 비어 있을 때") {
            it("빈 목록을 반환한다") {
                ConcertHallImageListResponse().toDomain().shouldBeEmpty()
            }
        }
    }
})
