package com.nexters.boolti.data.network.response

import com.nexters.boolti.data.util.toLocalDateTime
import com.nexters.boolti.domain.model.NewShowsAndRisingKeywords
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
internal data class SearchOverviewResponse(
    @SerialName("newCreatedShows")
    val newCreatedShows: List<ShowResponse>,
    @SerialName("popularSearchKeywords")
    val popularSearchKeywords: PopularSearchKeywordsDto,
)

@Serializable
internal data class PopularSearchKeywordsDto(
    @SerialName("referenceTime")
    val referenceTime: String,
    @SerialName("popularSearchKeywords")
    val popularSearchKeywords: List<RankAndKeywordDto>,
) {
    @Serializable
    data class RankAndKeywordDto(
        @SerialName("rank")
        val rank: Int,
        @SerialName("keyword")
        val keyword: String,
    )
}

internal fun SearchOverviewResponse.toNewShowsAndRisingKeywords(): NewShowsAndRisingKeywords =
    NewShowsAndRisingKeywords(
        newShows = newCreatedShows.map { it.toDomain() },
        risingKeywords = popularSearchKeywords.popularSearchKeywords.map { it.keyword },
        risingKeywordsTime = popularSearchKeywords.referenceTime.toLocalDateTime(),
    )
