package com.nexters.boolti.domain.model

data class SubwayLine(
    val id: String,
    val name: String,
    val colorHex: String,
) {
    /**
     * 서버 응답 값 예시 "수도권 1호선"
     * 비수도권 노선도와 혼동을 막기 위해 prefix가 붙은 것으로 보임. 따라서 이를 제거하기 위한 장치
     *
     * 노선 뱃지에 표기할 이름. ex. 수도권 1호선 -> 1, 2호선 -> 2, 신분당선 -> 신분당선
     */
    val displayName = name
        .removePrefix(METROPOLITAN_AREA_PREFIX)
        .removeSuffix(LINE_SUFFIX)
        .trim()

    companion object {
        private const val METROPOLITAN_AREA_PREFIX = "수도권"
        private const val LINE_SUFFIX = "호선"
    }
}
