package com.nexters.boolti.common.tracker.impression

/** 노출 기준을 충족한 순간의 로그 정보. [threshold]는 실제 비율이 아닌 설정한 기준임. */
data class ImpressionEvent(
    val key: Any,
    val threshold: Float,
    val extras: Map<String, Any>,
)
