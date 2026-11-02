package com.nexters.boolti.presentation.extension

import com.nexters.boolti.domain.model.SubwayLine

private val LINE_LABELS = mapOf(
    "SEOUL_LINE_1" to "1",
    "SEOUL_LINE_2" to "2",
    "SEOUL_LINE_3" to "3",
    "SEOUL_LINE_4" to "4",
    "SEOUL_LINE_5" to "5",
    "SEOUL_LINE_6" to "6",
    "SEOUL_LINE_7" to "7",
    "SEOUL_LINE_8" to "8",
    "SEOUL_LINE_9" to "9",
    "SEOUL_INCHEON_LINE_1" to "인천 1",
    "SEOUL_INCHEON_LINE_2" to "인천 2",
    "SEOUL_SUIN_BUNDANG" to "분당",
    "SEOUL_SHINBUNDANG" to "신분당",
    "SEOUL_GYEONGUI_JUNGANG" to "경의",
    "SEOUL_GYEONGCHUN" to "경춘",
    "SEOUL_AIRPORT_RAILROAD" to "공항",
    "SEOUL_UIJEONGBU_LRT" to "의정",
    "SEOUL_EVERLINE" to "용인",
    "SEOUL_GYEONGGANG" to "경강",
    "SEOUL_UI_SINSEOL" to "우이",
    "SEOUL_SEOHAE" to "서해",
    "SEOUL_GIMPO_GOLD" to "김포",
    "SEOUL_SILLIM" to "신림",
    "SEOUL_GTX_A" to "GTX-A",
    "BUSAN_LINE_1" to "부산 1",
    "BUSAN_LINE_2" to "부산 2",
    "BUSAN_LINE_3" to "부산 3",
    "BUSAN_LINE_4" to "부산 4",
    "BUSAN_DONGHAE" to "동해",
    "BUSAN_GIMHAE_LRT" to "부김",
    "DAEGU_LINE_1" to "대구 1",
    "DAEGU_LINE_2" to "대구 2",
    "DAEGU_LINE_3" to "대구 3",
    "DAEGU_DAEGYEONG" to "대경",
    "GWANGJU_LINE_1" to "광주 1",
    "DAEJEON_LINE_1" to "대전 1",
)

val SubwayLine.displayName: String
    get() = LINE_LABELS[key] ?: name
