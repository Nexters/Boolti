package com.nexters.boolti.presentation.util

import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.input.OffsetMapping
import androidx.compose.ui.text.input.TransformedText
import androidx.compose.ui.text.input.VisualTransformation

class PhoneNumberVisualTransformation(
    private val sep: Char = '-',
) : VisualTransformation {
    override fun filter(text: AnnotatedString): TransformedText {
        val filtered = text.text.filter { it.isDigit() }.run {
            substring(0..minOf(10, lastIndex))
        }
        val annotatedString = AnnotatedString.Builder().run {
            filtered.forEachIndexed { i, n ->
                if (i in listOf(3, 7)) append(sep)
                append(n)
            }
            toAnnotatedString()
        }

        return TransformedText(annotatedString, phoneNumberOffsetMapping)
    }

    private val phoneNumberOffsetMapping = object : OffsetMapping {
        override fun originalToTransformed(offset: Int): Int {
            return when (offset) {
                in 0 until 4 -> offset
                in 4 until 8 -> offset + 1
                else -> offset + 2
            }
        }

        override fun transformedToOriginal(offset: Int): Int {
            return when (offset) {
                in 0 until 4 -> offset
                in 4 until 9 -> offset - 1
                else -> offset - 2
            }
        }
    }
}
