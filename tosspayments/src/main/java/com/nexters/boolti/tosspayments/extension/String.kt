package com.nexters.boolti.tosspayments.extension

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
