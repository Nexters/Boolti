package com.nexters.boolti.domain.model

import io.kotest.core.spec.style.BehaviorSpec
import io.kotest.matchers.collections.shouldBeEmpty
import io.kotest.matchers.shouldBe

class PlaceImageTest : BehaviorSpec() {
    private fun placeImage(id: Long, sequence: Int) = PlaceImage(
        id = id,
        imageUrl = "https://image.boolti.in/$id",
        thumbnailUrl = "https://thumbnail.boolti.in/$id",
        sequence = sequence,
    )

    init {
        given("sequence 순서와 다르게 섞인 이미지 목록이 주어지고") {
            val images = listOf(
                placeImage(id = 13, sequence = 2),
                placeImage(id = 11, sequence = 0),
                placeImage(id = 12, sequence = 1),
            )

            `when`("웹에서 전달받은 imageIds 순서로 정렬하면") {
                val result = images.orderedBy(listOf(12, 13, 11))

                then("imageIds 순서를 그대로 따른다") {
                    result.map { it.id } shouldBe listOf(12L, 13L, 11L)
                }
            }

            `when`("imageIds가 비어 있으면") {
                val result = images.orderedBy(emptyList())

                then("sequence 순으로 정렬된 전체 목록을 반환한다") {
                    result.map { it.id } shouldBe listOf(11L, 12L, 13L)
                }
            }

            `when`("imageIds에 목록에 없는 id가 섞여 있으면") {
                val result = images.orderedBy(listOf(12, 99, 11))

                then("존재하지 않는 id는 무시한다") {
                    result.map { it.id } shouldBe listOf(12L, 11L)
                }
            }

            `when`("imageIds가 일부 이미지만 지정하면") {
                val result = images.orderedBy(listOf(13))

                then("지정된 이미지만 반환한다") {
                    result.map { it.id } shouldBe listOf(13L)
                }
            }
        }

        given("이미지 목록이 비어 있고") {
            val images = emptyList<PlaceImage>()

            `when`("imageIds로 정렬하면") {
                val result = images.orderedBy(listOf(11, 12))

                then("빈 목록을 반환한다") {
                    result.shouldBeEmpty()
                }
            }
        }
    }
}
