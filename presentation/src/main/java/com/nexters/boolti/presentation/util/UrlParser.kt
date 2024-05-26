package com.nexters.boolti.presentation.util

import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.style.TextDecoration

class UrlParser(
    url: String,
    urlRegex: Regex = "(https?://\\S+\\b)".toRegex(),
) {
    val annotatedString: AnnotatedString

    private val _urlOffsets = mutableListOf<UrlOffset>()
    val urlOffsets = _urlOffsets.toList()

    init {
        val linkMatch = urlRegex.toPattern().matcher(url)

        while (linkMatch.find()) {
            _urlOffsets.add(UrlOffset(linkMatch.start(), linkMatch.end()))
        }

        annotatedString = buildAnnotatedString {
            append(url)
            urlOffsets.forEach { (start, end) ->
                addStyle(
                    SpanStyle(
                        textDecoration = TextDecoration.Underline,
                        color = Color(0xFF46A6FF)
                    ),
                    start,
                    end,
                )
            }
        }
    }
}

data class UrlOffset(
    val start: Int,
    val end: Int
)