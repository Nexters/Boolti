package com.nexters.boolti.presentation.screen.place

/**
 * 도로명 주소의 시·도를 짧은 이름으로 바꾼다. (예: "서울특별시 마포구 ..." → "서울")
 *
 * 주소가 없거나 시·도로 시작하지 않으면 null.
 */
internal fun regionOf(streetAddress: String?): String? {
    val firstWord = streetAddress?.trim()?.substringBefore(' ') ?: return null
    return regionByName[firstWord]
}

private val regionByName: Map<String, String> = mapOf(
    "서울" to listOf("서울특별시", "서울시"),
    "부산" to listOf("부산광역시", "부산시"),
    "대구" to listOf("대구광역시", "대구시"),
    "인천" to listOf("인천광역시", "인천시"),
    "광주" to listOf("광주광역시", "광주시"),
    "대전" to listOf("대전광역시", "대전시"),
    "울산" to listOf("울산광역시", "울산시"),
    "세종" to listOf("세종특별자치시", "세종시"),
    "경기" to listOf("경기도"),
    "강원" to listOf("강원특별자치도", "강원도"),
    "충북" to listOf("충청북도"),
    "충남" to listOf("충청남도"),
    "전북" to listOf("전북특별자치도", "전라북도"),
    "전남" to listOf("전라남도"),
    "경북" to listOf("경상북도"),
    "경남" to listOf("경상남도"),
    "제주" to listOf("제주특별자치도", "제주도"),
).flatMap { (region, names) ->
    (names + region).map { it to region }
}.toMap()
