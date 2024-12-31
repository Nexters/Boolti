package com.nexters.boolti.tosspayments.extension

/**
 * ## 따옴표 변환
 *
 * 큰 따옴표(")를 열리는 큰 따옴표(“)와 닫히는 큰 따옴표(”)로 변환하거나
 * 작은 따옴표(')를 열리는 작은 따옴표(‘)와 닫히는 작은 따옴표(’)로 변환
 *
 * 토스페이먼츠에서 orderName에 따옴표가 들어간 경우 결제 렌더링이 안 되는 문제 해결을 위해 다른 문자로 대체하기 위해 사용함
 *
 * @see [TossPaymentWidgetActivity.getCommonIntent]
 * @return 따옴표가 변환된 문자열을 반환
 */
fun String.convertQuotes(): String = StringBuilder().apply {
    var isDoubleQuoteOpen = true
    var isSingleQuoteOpen = true

    this@convertQuotes.forEach { c ->
        when (c) {
            '"' -> {
                append(if (isDoubleQuoteOpen) "“" else "”")
                isDoubleQuoteOpen = !isDoubleQuoteOpen
            }

            '\'' -> {
                append(if (isSingleQuoteOpen) "‘" else "’")
                isSingleQuoteOpen = !isSingleQuoteOpen
            }

            else -> append(c)
        }
    }
}.toString()

private val encodingMap = mapOf(
    ':' to "%3A",
    '/' to "%2F",
    '?' to "%3F",
    '#' to "%23",
    '[' to "%5B",
    ']' to "%5D",
    '@' to "%40",
    '!' to "%21",
    '$' to "%24",
    '&' to "%26",
    '\'' to "%27",
    '(' to "%28",
    ')' to "%29",
    '*' to "%2A",
    '+' to "%2B",
    ',' to "%2C",
    ';' to "%3B",
    '=' to "%3D",
    '%' to "%25",
    ' ' to "+"
)

private val decodingMap = encodingMap.map { it.value to it.key }.toMap()

fun String.percentEncode(): String = fold(StringBuilder()) { sb, c ->
    sb.append(encodingMap.getOrDefault(c, c))
}.toString()

fun String.percentDecode(): String {
    val stringBuilder = StringBuilder()
    var i = 0
    while (i < this.length) {
        if (this[i] == '%' && i + 2 < this.length) {
            val encodedValue = this.substring(i, i + 3)
            val decodedChar = decodingMap.getOrDefault(encodedValue, encodedValue)
            stringBuilder.append(decodedChar)
            i += 3
        } else {
            stringBuilder.append(this[i])
            i++
        }
    }
    return stringBuilder.toString()
}
