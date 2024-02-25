package com.nexters.boolti.presentation.util

import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.input.OffsetMapping
import androidx.compose.ui.text.input.TransformedText
import androidx.compose.ui.text.input.VisualTransformation
import com.nexters.boolti.presentation.extension.filterToPhoneNumber
import com.nexters.boolti.presentation.extension.toContactFormat

class PhoneNumberVisualTransformation(
    private val sep: Char = '-',
) : VisualTransformation {
    override fun filter(text: AnnotatedString): TransformedText {
        val annotatedString = AnnotatedString(text.text.toContactFormat(sep))
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
