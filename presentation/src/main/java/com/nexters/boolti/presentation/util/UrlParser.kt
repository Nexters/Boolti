package com.nexters.boolti.presentation.util

import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.style.TextDecoration

class UrlParser(
    url: String
) {
    val annotatedString: AnnotatedString
    val urlOffsets = mutableListOf<UrlOffset>()

    init {
        val urlPattern = "(https?://\\S+\\b)".toRegex().toPattern()
        val linkMatch = urlPattern.matcher(url)

        while (linkMatch.find()) {
            urlOffsets.add(UrlOffset(linkMatch.start(), linkMatch.end()))
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